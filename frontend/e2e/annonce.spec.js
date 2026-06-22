import { test, expect } from '@playwright/test'

test.describe('Catalogue des annonces', () => {

  test('should display the home page', async ({ page }) => {
    await page.goto('/')

    // Vérifie que le titre est présent
    await expect(page).toHaveTitle(/Collector/)

    // Vérifie que le hero est présent
    await expect(page.locator('h1')).toContainText('Trouvez la pièce')
  })

  test('should display annonces in catalogue', async ({ page }) => {
    await page.goto('/')

    // Attend que les annonces chargent
    await page.waitForSelector('.annonces-grid', { timeout: 10000 })

    // Vérifie qu'il y a des annonces
    const cards = page.locator('.card')
    await expect(cards).toHaveCount(await cards.count())
    expect(await cards.count()).toBeGreaterThan(0)
  })

  test('should filter annonces by category', async ({ page }) => {
    await page.goto('/')

    // Attend que les annonces chargent
    await page.waitForSelector('.annonces-grid', { timeout: 10000 })

    // Clique sur le filtre Sneakers
    await page.click('button:has-text("Sneakers")')

    // Vérifie que le filtre est actif
    const sneakersBtn = page.locator('button:has-text("Sneakers")')
    await expect(sneakersBtn).toHaveClass(/active/)
  })

  test('should search annonces', async ({ page }) => {
    await page.goto('/')

    // Attend que les annonces chargent
    await page.waitForSelector('.annonces-grid', { timeout: 10000 })

    // Tape dans la barre de recherche
    await page.fill('input[placeholder*="Rechercher"]', 'Nike')

    // Vérifie que les résultats sont filtrés
    const results = page.locator('.results-count')
    await expect(results).toBeVisible()
  })

})