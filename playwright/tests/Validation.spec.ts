import { test, expect } from '@playwright/test';
import { generateUsername } from './utils';

/**
 * 입력 유효성 검증 테스트
 */
test.describe('입력 유효성 검증', () => {
  
  test('사용자명 길이 확인 (4~20자)', async ({ page }) => {
    const username = generateUsername();
    
    console.log('생성된 사용자명:', username);
    console.log('사용자명 길이:', username.length);
    
    // 4~20자 확인
    expect(username.length).toBeGreaterThanOrEqual(4);
    expect(username.length).toBeLessThanOrEqual(20);
    
    // 영문자와 숫자만 포함
    expect(username).toMatch(/^[a-zA-Z0-9]+$/);
  });
  
  test('비밀번호 길이 확인 (8자 이상)', async ({ page }) => {
    const password = 'test1234';
    
    console.log('비밀번호 길이:', password.length);
    
    expect(password.length).toBeGreaterThanOrEqual(8);
  });
  
  test('회원가입 폼 validation - 사용자명 너무 짧음', async ({ page }) => {
    await page.goto('/auth/signup');
    await page.waitForLoadState('networkidle');
    
    // 3자 입력 (최소 4자)
    await page.fill('#username', 'abc');
    await page.fill('#password', 'test1234');
    await page.fill('#passwordConfirm', 'test1234');
    await page.check('#agreeTerms', { force: true });
    
    await page.click('button[type="submit"]');
    
    // validation 메시지 확인
    const usernameInput = page.locator('#username');
    const validationMessage = await usernameInput.evaluate(
      (el: HTMLInputElement) => el.validationMessage
    );
    
    expect(validationMessage).toBeTruthy();
    console.log('Validation 메시지:', validationMessage);
  });
  
  test('회원가입 폼 validation - 사용자명 너무 김', async ({ page }) => {
    await page.goto('/auth/signup');
    await page.waitForLoadState('networkidle');
    
    // 21자 입력 (최대 20자)
    await page.fill('#username', 'a'.repeat(21));
    await page.fill('#password', 'test1234');
    await page.fill('#passwordConfirm', 'test1234');
    await page.check('#agreeTerms', { force: true });
    
    await page.click('button[type="submit"]');
    
    // validation 메시지 확인
    const usernameInput = page.locator('#username');
    const validationMessage = await usernameInput.evaluate(
      (el: HTMLInputElement) => el.validationMessage
    );
    
    expect(validationMessage).toBeTruthy();
    console.log('Validation 메시지:', validationMessage);
  });
  
  test('회원가입 폼 validation - 비밀번호 너무 짧음', async ({ page }) => {
    await page.goto('/auth/signup');
    await page.waitForLoadState('networkidle');
    
    await page.fill('#username', generateUsername());
    await page.fill('#password', 'test123'); // 7자 (최소 8자)
    await page.fill('#passwordConfirm', 'test123');
    await page.check('#agreeTerms', { force: true });
    
    await page.click('button[type="submit"]');
    
    // validation 메시지 확인
    const passwordInput = page.locator('#password');
    const validationMessage = await passwordInput.evaluate(
      (el: HTMLInputElement) => el.validationMessage
    );
    
    expect(validationMessage).toBeTruthy();
    console.log('Validation 메시지:', validationMessage);
  });
  
  test('사용자명 패턴 검증 - 특수문자 불가', async ({ page }) => {
    await page.goto('/auth/signup');
    await page.waitForLoadState('networkidle');
    
    // 특수문자 포함
    await page.fill('#username', 'test_user!');
    await page.fill('#password', 'test1234');
    await page.fill('#passwordConfirm', 'test1234');
    await page.check('#agreeTerms', { force: true });
    
    await page.click('button[type="submit"]');
    
    // validation 메시지 확인
    const usernameInput = page.locator('#username');
    const validationMessage = await usernameInput.evaluate(
      (el: HTMLInputElement) => el.validationMessage
    );
    
    expect(validationMessage).toBeTruthy();
    console.log('Validation 메시지:', validationMessage);
  });
});