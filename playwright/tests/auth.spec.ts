import { test, expect } from '@playwright/test';
import { generateUsername, signup, login, logout } from './utils';

test.describe('인증 기능 테스트', () => {
  
  test('회원가입 성공', async ({ page }) => {
    const username = generateUsername();
    
    await page.goto('/auth/signup');
    
    // 폼 확인
    await expect(page.locator('h2')).toContainText('Join Us');
    
    // 회원가입
    await page.fill('#username', username);
    await page.fill('#password', 'test1234');
    await page.fill('#passwordConfirm', 'test1234');
    
    // 비밀번호 강도 표시 확인
    await expect(page.locator('.strength-text')).toBeVisible();
    
    await page.check('#agreeTerms');
    
    // 제출
    page.once('dialog', dialog => {
      expect(dialog.message()).toContain('회원가입이 완료되었습니다');
      dialog.accept();
    });
    
    await page.click('button[type="submit"]');
    
    // 로그인 페이지로 리디렉션 확인
    await page.waitForURL('/auth/login');
    await expect(page.locator('h2')).toContainText('Welcome Back');
  });

  test('회원가입 - 비밀번호 불일치', async ({ page }) => {
    await page.goto('/auth/signup');
    
    await page.fill('#username', generateUsername());
    await page.fill('#password', 'test1234');
    await page.fill('#passwordConfirm', 'different');
    
    // 불일치 메시지 확인
    await expect(page.locator('#passwordMatch')).toContainText('일치하지 않습니다');
  });

  test('로그인 성공', async ({ page }) => {
    const username = generateUsername();
    
    // 먼저 회원가입
    await signup(page, username);
    
    // 로그인
    await page.goto('/auth/login');
    await page.fill('#username', username);
    await page.fill('#password', 'test1234');
    await page.click('button[type="submit"]');
    
    // 게시판으로 리디렉션 확인
    await page.waitForURL('/board');
    
    // 사용자명 표시 확인
    await expect(page.locator('.user-info')).toContainText(username);
  });

  test('로그인 실패 - 잘못된 비밀번호', async ({ page }) => {
    const username = generateUsername();
    await signup(page, username);
    
    await page.goto('/auth/login');
    await page.fill('#username', username);
    await page.fill('#password', 'wrongpassword');
    await page.click('button[type="submit"]');
    
    // 에러 메시지 확인
    await expect(page.locator('.alert-danger')).toContainText('일치하지 않습니다');
  });

  test('로그아웃 성공', async ({ page }) => {
    const username = generateUsername();
    await signup(page, username);
    await login(page, username);
    
    // 로그아웃
    await page.click('button[type="submit"]:has-text("로그아웃")');
    
    // 게시판 페이지 유지
    await expect(page).toHaveURL('/board');
    
    // 로그인 버튼 표시 확인
    await expect(page.locator('a:has-text("로그인")')).toBeVisible();
  });
});
