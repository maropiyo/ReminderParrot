const fs = require('fs');
const path = require('path');
const resemble = require('resemblejs');

const BASELINE_DIR = 'baseline';
const CURRENT_DIR = 'current';
const RESULT_DIR = 'comparison-results';

// 結果ディレクトリの作成
if (!fs.existsSync(RESULT_DIR)) {
  fs.mkdirSync(RESULT_DIR, { recursive: true });
}

// すべてのスクリーンショットを取得
const platforms = ['android', 'ios'];
let hasDifferences = false;

platforms.forEach(platform => {
  const currentPlatformDir = path.join(CURRENT_DIR, platform);

  if (!fs.existsSync(currentPlatformDir)) {
    console.log(`No screenshots found for ${platform}`);
    return;
  }

  // スクリーンショットファイルを取得
  const screenshots = fs.readdirSync(currentPlatformDir)
    .filter(file => file.endsWith('.png'));

  screenshots.forEach(screenshot => {
    const baselinePath = path.join(BASELINE_DIR, platform, screenshot);
    const currentPath = path.join(currentPlatformDir, screenshot);

    // ベースラインが存在しない場合は新しいスクリーンショットとして扱う
    if (!fs.existsSync(baselinePath)) {
      console.log(`New screenshot: ${screenshot} for ${platform}`);
      fs.copyFileSync(currentPath, path.join(RESULT_DIR, `${platform}_${screenshot}`));
      return;
    }

    // スクリーンショットを比較
    resemble(currentPath)
      .compareTo(baselinePath)
      .onComplete(data => {
        if (parseFloat(data.misMatchPercentage) > 0.1) {
          hasDifferences = true;
          console.log(`Difference found: ${screenshot} for ${platform} - ${data.misMatchPercentage}%`);

          // 差分画像を保存
          fs.writeFileSync(
            path.join(RESULT_DIR, `${platform}_${screenshot}_diff.png`),
            data.getBuffer()
          );
        }
      });
  });
});

// 差分がある場合はエラーステータスで終了
if (hasDifferences) {
  process.exit(1);
}
