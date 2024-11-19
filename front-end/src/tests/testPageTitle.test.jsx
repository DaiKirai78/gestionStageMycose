import { expect, test } from 'vitest'
import { render } from 'vitest-browser-react'
import PageTitle from '../components/pageTitle'

test('renders titre', async () => {
  const { getByText } = render(<PageTitle title="Vitest" />)

  await expect.element(getByText('Vitest')).toBeInTheDocument()
})