import { test, expect } from '@playwright/test'

const mockAnnonces = [
  {
    id: 1,
    titre: 'Nike Air Jordan 1 Chicago 1985',
    description: 'Paire originale en très bon état',
    prix: 1500,
    fraisPort: 10,
    categorie: 'SNEAKERS',
    statut: 'VALIDEE',
    vendeurId: 'user-1',
    vendeurUsername: 'testuser',
    imageUrl: null
  },
  {
    id: 2,
    titre: 'Vinyle Pink Floyd',
    description: 'Original pressing',
    prix: 120,
    fraisPort: 5,
    categorie: 'VINYLES',
    statut: 'VALIDEE',
    vendeurId: 'user-2',
    vendeurUsername: 'bob',
    imageUrl: null
  }
]

async function mockAPI(page) {
  await page.route('**/api/annonces**', route => {
    route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify(mockAnnonces)
    })
  })
}

test.describe('Catalogue des annonces', () => {

  test('should display the home page', async ({ page }) => {
    await mockAPI(page)
    await page.goto('/')
    await expect(page).toHaveTitle(/Collector/)
    await expect(page.locator('h1')).toContainText('Trouvez la pièce')
  })

  test('should display annonces in catalogue', async ({ page }) => {
    await mockAPI(page)
    await page.goto('/')
    await page.waitForSelector('.annonces-grid', { timeout: 15000 })
    const cards = page.locator('.card')
    expect(await cards.count()).toBeGreaterThan(0)
  })

  test('should filter annonces by category', async ({ page }) => {
    await mockAPI(page)
    await page.goto('/')
    await page.waitForSelector('.annonces-grid', { timeout: 15000 })
    await page.click('button:has-text("Sneakers")')
    const sneakersBtn = page.locator('button:has-text("Sneakers")')
    await expect(sneakersBtn).toHaveClass(/active/)
  })

  test('should search annonces', async ({ page }) => {
    await mockAPI(page)
    await page.goto('/')
    await page.waitForSelector('.annonces-grid', { timeout: 15000 })
    await page.fill('input[placeholder*="Rechercher"]', 'Nike')
    const results = page.locator('.results-count')
    await expect(results).toBeVisible()
  })

})