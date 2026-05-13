import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts'
import type { DepartmentInsight } from '@/types'

interface Props {
  data: DepartmentInsight[]
}

function formatK(value: number) {
  return value >= 1000 ? `${(value / 1000).toFixed(0)}k` : String(value)
}

function CustomTooltip({ active, payload, label }: {
  active?: boolean
  payload?: { value: number }[]
  label?: string
}) {
  if (!active || !payload?.length) return null
  return (
    <div className="rounded-lg border bg-card px-3 py-2 shadow text-sm">
      <p className="font-medium mb-1">{label}</p>
      <p className="text-muted-foreground">
        Avg salary: <span className="text-foreground font-mono">{payload[0].value.toLocaleString()}</span>
      </p>
    </div>
  )
}

export default function SalaryByDepartmentChart({ data }: Props) {
  const chartData = data.map(d => ({
    department: d.department,
    avgSalary: Math.round(d.averageSalary),
  }))

  return (
    <ResponsiveContainer width="100%" height={300}>
      <BarChart data={chartData} margin={{ top: 4, right: 16, left: 0, bottom: 4 }}>
        <CartesianGrid strokeDasharray="3 3" className="stroke-border" />
        <XAxis
          dataKey="department"
          tick={{ fontSize: 12 }}
          className="text-muted-foreground"
        />
        <YAxis
          tickFormatter={formatK}
          tick={{ fontSize: 12 }}
          className="text-muted-foreground"
          width={48}
        />
        <Tooltip content={<CustomTooltip />} />
        <Bar dataKey="avgSalary" radius={[4, 4, 0, 0]} className="fill-primary" />
      </BarChart>
    </ResponsiveContainer>
  )
}
