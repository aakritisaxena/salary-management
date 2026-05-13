import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { Toaster } from '@/components/ui/sonner'
import Navbar from '@/components/layout/Navbar'
import EmployeesPage from '@/pages/EmployeesPage'
import InsightsPage from '@/pages/InsightsPage'

const queryClient = new QueryClient({
  defaultOptions: { queries: { staleTime: 30_000, retry: 1 } },
})

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Navbar />
        <main className="max-w-7xl mx-auto w-full px-6 py-6">
          <Routes>
            <Route path="/" element={<EmployeesPage />} />
            <Route path="/insights" element={<InsightsPage />} />
          </Routes>
        </main>
      </BrowserRouter>
      <Toaster richColors position="top-right" />
    </QueryClientProvider>
  )
}
