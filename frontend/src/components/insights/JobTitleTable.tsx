import type { JobTitleInsight } from '@/types'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'

interface Props {
  data: JobTitleInsight[]
  currency: string
}

function fmt(value: number, currency: string) {
  try {
    return new Intl.NumberFormat('en', {
      style: 'currency',
      currency,
      maximumFractionDigits: 0,
    }).format(value)
  } catch {
    return `${currency} ${value.toLocaleString()}`
  }
}

export default function JobTitleTable({ data, currency }: Props) {
  if (data.length === 0) {
    return (
      <p className="text-sm text-muted-foreground py-6 text-center">
        No data for selected country
      </p>
    )
  }

  return (
    <div className="rounded-lg border bg-card overflow-hidden">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Job Title</TableHead>
            <TableHead className="text-right">Headcount</TableHead>
            <TableHead className="text-right">Avg Salary</TableHead>
            <TableHead className="text-right">Min Salary</TableHead>
            <TableHead className="text-right">Max Salary</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {data.map(row => (
            <TableRow key={row.jobTitle}>
              <TableCell className="font-medium">{row.jobTitle}</TableCell>
              <TableCell className="text-right tabular-nums">{row.headcount}</TableCell>
              <TableCell className="text-right tabular-nums">{fmt(row.averageSalary, currency)}</TableCell>
              <TableCell className="text-right tabular-nums">{fmt(Number(row.minSalary), currency)}</TableCell>
              <TableCell className="text-right tabular-nums">{fmt(Number(row.maxSalary), currency)}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
}
