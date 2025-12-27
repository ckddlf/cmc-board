import { test, expect } from '@playwright/test';
import { generateUsername, signup, login, createCategory } from './utils';

test.describe('카테고리 관리 테스트', () => {
  let username: string;

  test.beforeEach(async ({ page }) => {
    username = generateUsername();
    await signup(page, username);
    await login(page, username);
  });

  test('카테고리 관리 페이지 접근', async ({ page }) => {
    await page.goto('/admin/categories');
    
    // 페이지 제목 확인
    await expect(page.locator('h2')).toContainText('카테고리 관리');
    
    // 추가 폼 확인
    await expect(page.locator('#categoryName')).toBeVisible();
    
    // 목록 테이블 확인
    await expect(page.locator('table')).toBeVisible();
  });

  test('카테고리 추가', async ({ page }) => {
    await page.goto('/admin/categories');
    
    const categoryName = `카테고리_${Date.now()}`;
    
    await page.fill('#categoryName', categoryName);
    
    page.once('dialog', dialog => {
      expect(dialog.message()).toContain('추가되었습니다');
      dialog.accept();
    });
    
    await page.click('button:has-text("추가")');
    
    // 페이지 새로고침 대기
    await page.waitForTimeout(1000);
    
    // 추가된 카테고리 확인
    await expect(page.locator('table')).toContainText(categoryName);
  });

  test('카테고리 중복 추가 방지', async ({ page }) => {
    const categoryName = `중복테스트_${Date.now()}`;
    
    // 첫 번째 추가
    await createCategory(page, categoryName);
    
    // 두 번째 추가 시도
    await page.fill('#categoryName', categoryName);
    
    page.once('dialog', dialog => {
      expect(dialog.message()).toContain('이미 존재하는');
      dialog.accept();
    });
    
    await page.click('button:has-text("추가")');
  });

  test('카테고리 수정', async ({ page }) => {
    const oldName = `수정전_${Date.now()}`;
    const newName = `수정후_${Date.now()}`;
    
    await createCategory(page, oldName);
    await page.reload();
    
    // 수정 버튼 클릭
    await page.click('button:has-text("수정")').first();
    
    // 입력창 확인
    await expect(page.locator('.category-edit-input').first()).toBeVisible();
    
    // 이름 변경
    await page.fill('.category-edit-input', newName);
    
    page.once('dialog', dialog => {
      expect(dialog.message()).toContain('수정되었습니다');
      dialog.accept();
    });
    
    await page.click('button:has-text("저장")').first();
    
    await page.waitForTimeout(1000);
    
    // 수정 확인
    await expect(page.locator('table')).toContainText(newName);
    await expect(page.locator('table')).not.toContainText(oldName);
  });

  test('카테고리 수정 취소', async ({ page }) => {
    const categoryName = `취소테스트_${Date.now()}`;
    
    await createCategory(page, categoryName);
    await page.reload();
    
    // 수정 버튼 클릭
    await page.click('button:has-text("수정")').first();
    
    // 이름 변경
    await page.fill('.category-edit-input', '변경된 이름');
    
    // 취소 버튼 클릭
    await page.click('button:has-text("취소")').first();
    
    // 원래 이름 유지 확인
    await expect(page.locator('table')).toContainText(categoryName);
  });

  test('카테고리 삭제', async ({ page }) => {
    const categoryName = `삭제테스트_${Date.now()}`;
    
    await createCategory(page, categoryName);
    await page.reload();
    
    // 삭제 확인 다이얼로그
    page.once('dialog', dialog => {
      expect(dialog.message()).toContain('삭제하시겠습니까');
      dialog.accept();
    });
    
    await page.click('button:has-text("삭제")').first();
    
    page.once('dialog', dialog => {
      expect(dialog.message()).toContain('삭제되었습니다');
      dialog.accept();
    });
    
    await page.waitForTimeout(1000);
    
    // 삭제 확인
    await expect(page.locator('table')).not.toContainText(categoryName);
  });

  test('네비게이션 메뉴에 카테고리 관리 링크 표시', async ({ page }) => {
    await page.goto('/board');
    
    // 로그인한 사용자는 카테고리 관리 링크 보임
    await expect(page.locator('a:has-text("카테고리 관리")')).toBeVisible();
    
    // 클릭 시 이동
    await page.click('a:has-text("카테고리 관리")');
    await expect(page).toHaveURL('/admin/categories');
  });
});
