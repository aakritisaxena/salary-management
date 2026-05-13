import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { insightsApi } from '@/api/employees'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Separator } from '@/components/ui/separator'
import StatCards from '@/components/insights/StatCards'
import SalaryByDepartmentChart from '@/components/insights/SalaryByDepartmentChart'
import JobTitleTable from '@/components/insights/JobTitleTable'

const COUNTRY_CURRENCY: Record<string, string> = {
  IN: 'INR', US: 'USD', GB: 'GBP', DE: 'EUR',
  CA: 'CAD', AU: 'AUD', SG: 'SGD', JP: 'JPY',
  AE: 'AED', BR: 'BRL',
}

function fmt(value: number | undefined, currency: string) {
  if (value === undefined) return '—'
  try {
    return new Intl.NumberFormat('en', {
      style: 'currency',
      currency,
      maximumFractionDigits: 0,
      notation: 'compact',
    }).format(value)
  } catch {
    return `${currency} ${value?.toLocaleString()}`
  }
}

export default function InsightsPage() {
  const [country, setCountry] = useState('IN')

  const { data: insights, isLoading } = useQuery({
    queryKey: ['insights', 'global'],
    queryFn: () => insightsApi.getInsights(),
  })

  const { data: jobTitles = [], isLoading: jobTitlesLoading } = useQuery({
    queryKey: ['insights', 'job-titles', country],
    queryFn: () => insightsApi.getJobTitleInsights(country),
  })

  const countries = insights?.byCountry.map(c => c.country).sort() ?? []
  const countryData = insights?.byCountry.find(c => c.country === country)
  const currency = COUNTRY_CURRENCY[country] ?? 'USD'

  const statCards = [
    { label: 'Total Employees', value: countryData?.headcount.toLocaleString() ?? '—' },
    { label: 'Avg Salary', value: fmt(countryData?.averageSalary, currency) },
    { label: 'Min Salary', value: fmt(Number(countryData?.minSalary), currency) },
    { label: 'Max Salary', value: fmt(Number(countryData?.maxSalary), currency) },
  ]

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Insights</h1>
          <p className="text-sm text-muted-foreground mt-0.5">
            Salary analytics across your workforce
          </p>
        </div>
        <Select value={country} onValueChange={setCountry}>
          <SelectTrigger className="w-36">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            {countries.map(c => (
              <SelectItem key={c} value={c}>{c}</SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      {isLoading ? (
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="h-24 rounded-lg border bg-card animate-pulse" />
          ))}
        </div>
      ) : (
        <StatCards stats={statCards} />
      )}

      <Separator />

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Average Salary by Department</CardTitle>
          <p className="text-xs text-muted-foreground">Global — across all countries</p>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="h-[300px] bg-muted rounded animate-pulse" />
          ) : (
            <SalaryByDepartmentChart data={insights?.byDepartment ?? []} />
          )}
        </CardContent>
      </Card>

      <Separator />

      <div>
        <h2 className="text-base font-semibold mb-1">
          Salary by Job Title — {country}
        </h2>
        <p className="text-sm text-muted-foreground mb-4">
          Sorted by average salary, highest first
        </p>
        {jobTitlesLoading ? (
          <div className="h-40 rounded-lg border bg-muted animate-pulse" />
        ) : (
          <JobTitleTable data={jobTitles} currency={currency} />
        )}
      </div>
    </div>
  )
}
