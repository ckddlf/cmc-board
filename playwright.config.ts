import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './tests',
  
  // 테스트 타임아웃
  timeout: 30 * 1000,
  expect: {
    timeout: 5000
  },
  
  // 병렬 실행
  fullyParallel: true,
  
  // 실패 시 재시도
  retries: process.env.CI ? 2 : 0,
  
  // Worker 수
  workers: process.env.CI ? 1 : undefined,
  
  // 리포터
  reporter: [
    ['html'],
    ['list']
  ],
  
  // 공통 설정
  use: {
    // Base URL
    baseURL: 'http://localhost:8080',
    
    // 스크린샷
    screenshot: 'only-on-failure',
    
    // 비디오
    video: 'retain-on-failure',
    
    // 트레이스
    trace: 'on-first-retry',
  },

  // 프로젝트별 설정
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
    // 모바일 테스트
    {
      name: 'Mobile Chrome',
      use: { ...devices['Pixel 5'] },
    },
    {
      name: 'Mobile Safari',
      use: { ...devices['iPhone 12'] },
    },
  ],

  // 로컬 서버 실행 (선택사항)
  // webServer: {
  //   command: 'mvn spring-boot:run',
  //   url: 'http://localhost:8080',
  //   reuseExistingServer: !process.env.CI,
  // },
});
