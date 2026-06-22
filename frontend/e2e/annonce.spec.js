import { test, expect } from '@playwright/test'

test.describe('Catalogue des annonces', () => {

  test('should display the home page', async ({ page }) => {
    await page.goto('/')
    await expect(page).toHaveTitle(/Collector/)
    await expect(page.locator('h1')).toContainText('Trouvez la pièce')
  })

  test('should display search bar', async ({ page }) => {
    await page.goto('/')
    await page.waitForLoadState('networkidle')
    const searchBar = page.locator('input[placeholder*="Rechercher"]')
    await expect(searchBar).toBeVisible()
  })

  test('should display category filters', async ({ page }) => {
    await page.goto('/')
    await page.waitForLoadState('networkidle')
    const sneakersBtn = page.locator('button:has-text("Sneakers")')
    await expect(sneakersBtn).toBeVisible()
  })

  test('should filter by category on click', async ({ page }) => {
    await page.goto('/')
    await page.waitForLoadState('networkidle')
    await page.click('button:has-text("Sneakers")')
    const sneakersBtn = page.locator('button:has-text("Sneakers")')
    await expect(sneakersBtn).toHaveClass(/active/)
  })

  test('should search and show results count', async ({ page }) => {
    await page.goto('/')
    await page.waitForLoadState('networkidle')
    await page.fill('input[placeholder*="Rechercher"]', 'Nike')
    const results = page.locator('.results-count')
    await expect(results).toBeVisible()
  })

  test('should display navbar', async ({ page }) => {
    await page.goto('/')
    await expect(page.locator('.logo-text')).toContainText('Collector')
  })

})