import { serve } from "https://deno.land/std@0.208.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2.39.0"
import { SignJWT, importPKCS8 } from "https://deno.land/x/jose@v5.1.0/index.ts"

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

interface NotificationRequest {
  postId: string
  postUserId: string
  senderUserId: string
  senderUserName: string
  title: string
  body: string
  notificationType: string
}

serve(async (req) => {
  // Handle CORS preflight requests
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders })
  }

  try {
    const supabaseUrl = Deno.env.get('SUPABASE_URL')!
    const supabaseServiceKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')!
    const fcmProjectId = Deno.env.get('FCM_PROJECT_ID')
    const fcmServiceAccount = Deno.env.get('FCM_SERVICE_ACCOUNT')

    const supabase = createClient(supabaseUrl, supabaseServiceKey)
    
    const notification: NotificationRequest = await req.json()
    console.log('通知リクエスト受信:', notification)

    // 投稿者のプッシュ通知トークンを取得
    const { data: tokens, error: tokenError } = await supabase
      .from('push_tokens')
      .select('token, platform')
      .eq('user_id', notification.postUserId)

    if (tokenError) {
      throw new Error(`トークン取得エラー: ${tokenError.message}`)
    }

    if (!tokens || tokens.length === 0) {
      console.log('送信先のプッシュ通知トークンが見つかりません')
      return new Response(
        JSON.stringify({ success: true, message: 'トークンが登録されていません' }),
        { headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
      )
    }

    // FCMサービスアカウント認証情報を確認
    if (!fcmProjectId || !fcmServiceAccount) {
      console.log('FCM設定が不完全です（モックモードで動作）')
      console.log('実際の通知は送信されませんが、処理は成功として扱います')
      
      return new Response(
        JSON.stringify({ 
          success: true, 
          message: 'FCM設定なし（モックモード）',
          mockData: {
            tokens: tokens.length,
            title: notification.title,
            body: notification.body
          }
        }),
        { headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
      )
    }

    // サービスアカウントをパース
    const serviceAccount = JSON.parse(fcmServiceAccount)
    
    // アクセストークンを取得
    const accessToken = await getAccessToken(serviceAccount)
    
    // 各トークンに通知を送信
    const results = await Promise.all(
      tokens.map(async (tokenData) => {
        try {
          await sendFcmNotification(
            fcmProjectId,
            accessToken,
            tokenData.token,
            notification.title,
            notification.body,
            tokenData.platform
          )
          return { token: tokenData.token, success: true }
        } catch (error) {
          console.error(`通知送信エラー (${tokenData.token}):`, error)
          return { token: tokenData.token, success: false, error: error.message }
        }
      })
    )

    const successCount = results.filter(r => r.success).length
    console.log(`通知送信完了: ${successCount}/${results.length} 成功`)

    return new Response(
      JSON.stringify({ 
        success: true, 
        results,
        summary: {
          total: results.length,
          success: successCount,
          failed: results.length - successCount
        }
      }),
      { headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
    )

  } catch (error) {
    console.error('エラー:', error)
    return new Response(
      JSON.stringify({ error: error.message }),
      { 
        status: 500,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      }
    )
  }
})

// Google OAuth2でアクセストークンを取得
async function getAccessToken(serviceAccount: any): Promise<string> {
  // 秘密鍵をインポート
  const privateKey = await importPKCS8(serviceAccount.private_key, 'RS256')
  
  // JWTを作成
  const jwt = await new SignJWT({
    scope: 'https://www.googleapis.com/auth/firebase.messaging'
  })
    .setProtectedHeader({ alg: 'RS256' })
    .setIssuedAt()
    .setIssuer(serviceAccount.client_email)
    .setSubject(serviceAccount.client_email)
    .setAudience('https://oauth2.googleapis.com/token')
    .setExpirationTime('1h')
    .sign(privateKey)

  // アクセストークンを取得
  const tokenResponse = await fetch('https://oauth2.googleapis.com/token', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: new URLSearchParams({
      grant_type: 'urn:ietf:params:oauth:grant-type:jwt-bearer',
      assertion: jwt
    })
  })

  if (!tokenResponse.ok) {
    throw new Error(`トークン取得失敗: ${await tokenResponse.text()}`)
  }

  const tokenData = await tokenResponse.json()
  return tokenData.access_token
}

// FCM HTTP v1 APIで通知送信
async function sendFcmNotification(
  projectId: string,
  accessToken: string,
  token: string,
  title: string,
  body: string,
  platform: string
): Promise<void> {
  const fcmUrl = `https://fcm.googleapis.com/v1/projects/${projectId}/messages:send`

  const message = {
    message: {
      token: token,
      notification: {
        title: title,
        body: body
      },
      // プラットフォーム別の設定
      ...(platform === 'ANDROID' ? {
        android: {
          priority: 'high',
          notification: {
            sound: 'default',
            channel_id: 'remindnet_notification_channel'
          }
        }
      } : {}),
      ...(platform === 'IOS' ? {
        apns: {
          payload: {
            aps: {
              alert: {
                title: title,
                body: body
              },
              sound: 'default'
            }
          }
        }
      } : {})
    }
  }

  const response = await fetch(fcmUrl, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(message)
  })

  if (!response.ok) {
    const error = await response.text()
    throw new Error(`FCM送信エラー: ${error}`)
  }
}