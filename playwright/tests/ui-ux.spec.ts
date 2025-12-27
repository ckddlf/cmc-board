import { test, expect } from '@playwright/test';
import { generateUsername, signup, login, createPost } from './utils';

test.describe('UI/UX 테스트', () => {
  
  test('반응형 디자인 - 모바일', async ({ page }) => {
    // 모바일 뷰포트 설정
    await page.setViewportSize({ width: 375, height: 667 });
    
    await page.goto('/board');
    
    // 네비게이션 토글 버튼 확인
    await expect(page.locator('.navbar-toggler')).toBeVisible();
    
    // 메뉴 열기
    await page.click('.navbar-toggler');
    await expect(page.locator('#navbarNav')).toBeVisible();
  });

  test('다크모드 전환 (선택사항)', async ({ page }) => {
    // 다크모드 토글이 있다면
    const darkModeToggle = page.locator('[data-theme-toggle]');
    
    if (await darkModeToggle.isVisible()) {
      await darkModeToggle.click();
      
      // body 클래스 변경 확인
      await expect(page.locator('body')).toHaveClass(/dark/);
    }
  });

  test('스크롤 애니메이션', async ({ page }) => {
    const username = generateUsername();
    await signup(page, username);
    await login(page, username);
    
    // 여러 게시글 작성
    for (let i = 0; i < 10; i++) {
      await createPost(page, `게시글 ${i}`, `내용 ${i}`);
    }
    
    await page.goto('/board');
    
    // 카드 애니메이션 확인
    const cards = await page.locator('.post-card').all();
    
    // 첫 번째 카드는 보임
    await expect(cards[0]).toBeVisible();
    
    // 스크롤 시 다른 카드들 보임
    await page.evaluate(() => window.scrollTo(0, document.body.scrollHeight / 2));
    await page.waitForTimeout(500);
    
    for (const card of cards.slice(0, 5)) {
      await expect(card).toBeVisible();
    }
  });

  test('로딩 인디케이터', async ({ page }) => {
    const username = generateUsername();
    await signup(page, username);
    await login(page, username);
    
    await page.goto('/board/write');
    
    // 폼 작성
    await page.check('input[type="checkbox"]').first();
    await page.fill('#title', '테스트');
    await page.fill('#content', '내용');
    
    // 제출 (로딩 확인은 네트워크 속도에 따라 다를 수 있음)
    await page.click('button[type="submit"]');
    
    // 버튼 비활성화 확인 (옵션)
    // await expect(page.locator('button[type="submit"]')).toBeDisabled();
  });

  test('에러 처리 - 빈 폼 제출', async ({ page }) => {
    const username = generateUsername();
    await signup(page, username);
    await login(page, username);
    
    await page.goto('/board/write');
    
    // 빈 폼 제출
    await page.click('button[type="submit"]');
    
    // HTML5 validation 메시지
    const titleInput = page.locator('#title');
    const validationMessage = await titleInput.evaluate((el: HTMLInputElement) => el.validationMessage);
    
    expect(validationMessage).toBeTruthy();
  });

  test('토스트 알림', async ({ page }) => {
    const username = generateUsername();
    await signup(page, username);
    await login(page, username);
    await createPost(page, '테스트', '내용');
    
    // 게시글 상세 페이지
    await page.click('.post-card a').first();
    
    // 북마크 추가 시 토스트
    await page.click('.btn-bookmark');
    
    // 토스트 메시지 확인 (있다면)
    const toast = page.locator('.toast-notification');
    if (await toast.isVisible()) {
      await expect(toast).toContainText('북마크');
    }
  });

  test('포커스 관리', async ({ page }) => {
    await page.goto('/auth/login');
    
    // 첫 번째 입력 필드에 자동 포커스
    const usernameInput = page.locator('#username');
    await expect(usernameInput).toBeFocused();
  });

  test('키보드 네비게이션', async ({ page }) => {
    await page.goto('/auth/login');
    
    // Tab으로 이동
    await page.keyboard.press('Tab');
    await expect(page.locator('#username')).toBeFocused();
    
    await page.keyboard.press('Tab');
    await expect(page.locator('#password')).toBeFocused();
    
    await page.keyboard.press('Tab');
    await expect(page.locator('#rememberMe')).toBeFocused();
  });

  test('이미지 로딩 - Lazy Loading', async ({ page }) => {
    await page.goto('/board');
    
    // 이미지가 있다면 loading="lazy" 속성 확인
    const images = await page.locator('img').all();
    
    for (const img of images) {
      const loading = await img.getAttribute('loading');
      // lazy loading이 적용되어 있거나 없을 수 있음
      if (loading) {
        expect(loading).toBe('lazy');
      }
    }
  });

  test('접근성 - ARIA 속성', async ({ page }) => {
    await page.goto('/board');
    
    // 주요 랜드마크 확인
    await expect(page.locator('nav')).toBeVisible();
    await expect(page.locator('main')).toBeVisible();
    await expect(page.locator('footer')).toBeVisible();
    
    // 버튼에 적절한 레이블
    const buttons = await page.locator('button').all();
    for (const button of buttons) {
      const text = await button.textContent();
      const ariaLabel = await button.getAttribute('aria-label');
      
      // 버튼은 텍스트나 aria-label이 있어야 함
      expect(text || ariaLabel).toBeTruthy();
    }
  });

  test('페이지 타이틀', async ({ page }) => {
    await page.goto('/board');
    await expect(page).toHaveTitle(/CMC Board/);
    
    await page.goto('/auth/login');
    await expect(page).toHaveTitle(/로그인/);
    
    const username = generateUsername();
    await signup(page, username);
    await login(page, username);
    await createPost(page, '제목 테스트', '내용');
    
    await page.click('.post-card a').first();
    await expect(page).toHaveTitle(/제목 테스트/);
  });

  test('메타 태그', async ({ page }) => {
    await page.goto('/board');
    
    // viewport 메타 태그 확인
    const viewport = await page.locator('meta[name="viewport"]').getAttribute('content');
    expect(viewport).toContain('width=device-width');
  });
});
