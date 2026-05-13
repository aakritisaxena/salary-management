import { useEffect } from 'react'
import { useForm, type Resolver } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import type { Employee, EmployeeRequest } from '@/types'

const COUNTRY_CURRENCY: Record<string, string> = {
  IN: 'INR',
  US: 'USD',
  GB: 'GBP',
  DE: 'EUR',
  CA: 'CAD',
  AU: 'AUD',
  SG: 'SGD',
  JP: 'JPY',
}

const DEPARTMENTS = [
  'Engineering', 'Product', 'Design', 'Marketing',
  'Sales', 'Finance', 'HR', 'Operations',
]

const schema = z.object({
  fullName: z.string().min(1, 'Required').max(200),
  jobTitle: z.string().min(1, 'Required').max(100),
  department: z.string().min(1, 'Required'),
  country: z.string().min(1, 'Required'),
  currency: z.string().length(3, 'Must be 3 characters'),
  salary: z.coerce.number().min(0, 'Cannot be negative'),
  email: z.string().email('Invalid email'),
  hireDate: z.string().refine(
    (d) => new Date(d) <= new Date(),
    'Hire date cannot be in the future'
  ),
})

type FormValues = z.infer<typeof schema>

interface Props {
  open: boolean
  employee?: Employee | null
  onClose: () => void
  onSubmit: (data: EmployeeRequest) => void
  isLoading?: boolean
}

export default function EmployeeForm({ open, employee, onClose, onSubmit, isLoading }: Props) {
  const form = useForm<FormValues>({
    resolver: zodResolver(schema) as Resolver<FormValues>,
    defaultValues: {
      fullName: '',
      jobTitle: '',
      department: '',
      country: '',
      currency: '',
      salary: 0,
      email: '',
      hireDate: '',
    },
  })

  useEffect(() => {
    if (open) {
      form.reset(
        employee
          ? {
              fullName: employee.fullName,
              jobTitle: employee.jobTitle,
              department: employee.department,
              country: employee.country,
              currency: employee.currency,
              salary: employee.salary,
              email: employee.email,
              hireDate: employee.hireDate,
            }
          : {
              fullName: '',
              jobTitle: '',
              department: '',
              country: '',
              currency: '',
              salary: 0,
              email: '',
              hireDate: '',
            }
      )
    }
  }, [open, employee, form])

  function handleCountryChange(country: string, onChange: (v: string) => void) {
    onChange(country)
    const mapped = COUNTRY_CURRENCY[country]
    if (mapped) form.setValue('currency', mapped)
  }

  return (
    <Dialog open={open} onOpenChange={(v) => !v && onClose()}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>{employee ? 'Edit Employee' : 'Add Employee'}</DialogTitle>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="fullName"
                render={({ field }) => (
                  <FormItem className="col-span-2">
                    <FormLabel>Full Name</FormLabel>
                    <FormControl><Input {...field} /></FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="jobTitle"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Job Title</FormLabel>
                    <FormControl><Input {...field} /></FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="department"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Department</FormLabel>
                    <Select value={field.value} onValueChange={field.onChange}>
                      <FormControl>
                        <SelectTrigger><SelectValue placeholder="Select…" /></SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {DEPARTMENTS.map((d) => (
                          <SelectItem key={d} value={d}>{d}</SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem className="col-span-2">
                    <FormLabel>Email</FormLabel>
                    <FormControl><Input type="email" {...field} /></FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="country"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Country</FormLabel>
                    <Select
                      value={field.value}
                      onValueChange={(v) => handleCountryChange(v, field.onChange)}
                    >
                      <FormControl>
                        <SelectTrigger><SelectValue placeholder="Select…" /></SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {Object.keys(COUNTRY_CURRENCY).map((c) => (
                          <SelectItem key={c} value={c}>{c}</SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="currency"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Currency</FormLabel>
                    <FormControl>
                      <Input maxLength={3} placeholder="e.g. INR" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="salary"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Salary</FormLabel>
                    <FormControl><Input type="number" min={0} {...field} /></FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="hireDate"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Hire Date</FormLabel>
                    <FormControl>
                      <Input
                        type="date"
                        max={new Date().toISOString().split('T')[0]}
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <DialogFooter>
              <Button type="button" variant="outline" onClick={onClose}>
                Cancel
              </Button>
              <Button type="submit" disabled={isLoading}>
                {isLoading ? 'Saving…' : employee ? 'Update' : 'Create'}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  )
}
