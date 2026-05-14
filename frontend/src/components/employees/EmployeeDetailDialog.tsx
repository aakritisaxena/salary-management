import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Badge } from '@/components/ui/badge'
import { Separator } from '@/components/ui/separator'
import type { Employee } from '@/types'

interface Props {
  employee: Employee | null
  onClose: () => void
}

const COUNTRY_CURRENCY: Record<string, string> = {
  IN: 'INR', US: 'USD', GB: 'GBP', DE: 'EUR',
  CA: 'CAD', AU: 'AUD', SG: 'SGD', JP: 'JPY',
  AE: 'AED', BR: 'BRL',
}

function fmt(salary: number, currency: string) {
  try {
    return new Intl.NumberFormat('en', {
      style: 'currency',
      currency,
      maximumFractionDigits: 0,
    }).format(salary)
  } catch {
    return `${currency} ${salary.toLocaleString()}`
  }
}

function Field({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <div>
      <p className="text-xs text-muted-foreground mb-0.5">{label}</p>
      <p className="text-sm font-medium">{value}</p>
    </div>
  )
}

export default function EmployeeDetailDialog({ employee, onClose }: Props) {
  if (!employee) return null

  const currency = employee.currency || COUNTRY_CURRENCY[employee.country] || 'USD'

  return (
    <Dialog open={!!employee} onOpenChange={(v) => !v && onClose()}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>{employee.fullName}</DialogTitle>
          <p className="text-sm text-muted-foreground">{employee.jobTitle}</p>
        </DialogHeader>

        <div className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Field label="Department" value={<Badge variant="secondary">{employee.department}</Badge>} />
            <Field label="Country" value={employee.country} />
            <Field label="Email" value={employee.email} />
            <Field label="Hire Date" value={employee.hireDate} />
          </div>

          <Separator />

          <div>
            <p className="text-xs text-muted-foreground mb-1">Compensation</p>
            <p className="text-2xl font-semibold tabular-nums">
              {fmt(employee.salary, currency)}
            </p>
            <p className="text-xs text-muted-foreground mt-0.5">{currency} · per year</p>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}
