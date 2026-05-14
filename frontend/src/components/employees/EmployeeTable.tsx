import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { useDebounce } from 'use-debounce'
import { ChevronLeft, ChevronRight, Pencil, Trash2, Plus, X, Search } from 'lucide-react'
import { employeeApi, insightsApi } from '@/api/employees'
import type { Employee } from '@/types'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'

const PAGE_SIZE = 50

const DEPARTMENTS = [
  'Engineering', 'Product', 'Design', 'Marketing',
  'Sales', 'Finance', 'HR', 'Operations',
]

function SkeletonRow() {
  return (
    <TableRow>
      {Array.from({ length: 6 }).map((_, i) => (
        <TableCell key={i}>
          <div className="h-4 bg-muted rounded animate-pulse" />
        </TableCell>
      ))}
    </TableRow>
  )
}

interface Props {
  onAdd: () => void
  onEdit: (employee: Employee) => void
  onDelete: (employee: Employee) => void
  onView: (employee: Employee) => void
}

export default function EmployeeTable({ onAdd, onEdit, onDelete, onView }: Props) {
  const [page, setPage] = useState(0)
  const [country, setCountry] = useState<string>('')
  const [department, setDepartment] = useState<string>('')
  const [nameInput, setNameInput] = useState<string>('')
  const [debouncedName] = useDebounce(nameInput, 400)

  const { data: insights } = useQuery({
    queryKey: ['insights', 'headcount'],
    queryFn: () => insightsApi.getInsights(),
  })

  const { data, isLoading } = useQuery({
    queryKey: ['employees', page, country, department, debouncedName],
    queryFn: () =>
      employeeApi.list({
        page,
        size: PAGE_SIZE,
        country: country || undefined,
        department: department || undefined,
        name: debouncedName || undefined,
      }),
  })

  const countries = insights?.byCountry.map(c => c.country).sort() ?? []
  const employees = data?.content ?? []
  const hasFilters = !!country || !!department || !!nameInput

  function clearFilters() {
    setCountry('')
    setDepartment('')
    setNameInput('')
    setPage(0)
  }

  function handleCountryChange(val: string) {
    setCountry(val === 'all' ? '' : val)
    setPage(0)
  }

  function handleDepartmentChange(val: string) {
    setDepartment(val === 'all' ? '' : val)
    setPage(0)
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Employees</h1>
          <p className="text-sm text-muted-foreground mt-0.5">
            {data?.totalElements.toLocaleString() ?? '—'} total · click a name to view details
          </p>
        </div>
        <Button onClick={onAdd}>
          <Plus size={15} className="mr-1.5" />
          Add Employee
        </Button>
      </div>

      <div className="flex flex-wrap items-center gap-2">
        <div className="relative">
          <Search size={14} className="absolute left-2.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Search by name…"
            value={nameInput}
            onChange={e => { setNameInput(e.target.value); setPage(0) }}
            className="pl-8 w-52"
          />
        </div>

        <Select value={country || 'all'} onValueChange={handleCountryChange}>
          <SelectTrigger className="w-36">
            <SelectValue placeholder="Country" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Countries</SelectItem>
            {countries.map(c => (
              <SelectItem key={c} value={c}>{c}</SelectItem>
            ))}
          </SelectContent>
        </Select>

        <Select value={department || 'all'} onValueChange={handleDepartmentChange}>
          <SelectTrigger className="w-44">
            <SelectValue placeholder="Department" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Departments</SelectItem>
            {DEPARTMENTS.map(d => (
              <SelectItem key={d} value={d}>{d}</SelectItem>
            ))}
          </SelectContent>
        </Select>

        {hasFilters && (
          <Button variant="ghost" size="sm" onClick={clearFilters}>
            <X size={13} className="mr-1" />
            Clear filters
          </Button>
        )}
      </div>

      <div className="rounded-lg border bg-card overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Job Title</TableHead>
              <TableHead>Department</TableHead>
              <TableHead>Country</TableHead>
              <TableHead>Hire Date</TableHead>
              <TableHead className="w-16" />
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading ? (
              Array.from({ length: 8 }).map((_, i) => <SkeletonRow key={i} />)
            ) : employees.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-16 text-muted-foreground">
                  No employees found
                </TableCell>
              </TableRow>
            ) : (
              employees.map(emp => (
                <TableRow key={emp.id} className="cursor-pointer" onClick={() => onView(emp)}>
                  <TableCell>
                    <span className="font-medium hover:underline underline-offset-2">
                      {emp.fullName}
                    </span>
                  </TableCell>
                  <TableCell>{emp.jobTitle}</TableCell>
                  <TableCell>
                    <Badge variant="secondary">{emp.department}</Badge>
                  </TableCell>
                  <TableCell>{emp.country}</TableCell>
                  <TableCell>{emp.hireDate}</TableCell>
                  <TableCell>
                    <div className="flex gap-1" onClick={e => e.stopPropagation()}>
                      <Button
                        size="icon"
                        variant="ghost"
                        className="h-7 w-7"
                        onClick={() => onEdit(emp)}
                      >
                        <Pencil size={13} />
                      </Button>
                      <Button
                        size="icon"
                        variant="ghost"
                        className="h-7 w-7 text-destructive hover:text-destructive"
                        onClick={() => onDelete(emp)}
                      >
                        <Trash2 size={13} />
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      {data && data.totalPages > 1 && (
        <div className="flex items-center justify-between text-sm text-muted-foreground">
          <span>Page {page + 1} of {data.totalPages}</span>
          <div className="flex gap-2">
            <Button variant="outline" size="sm" disabled={data.first} onClick={() => setPage(p => p - 1)}>
              <ChevronLeft size={14} />
              Previous
            </Button>
            <Button variant="outline" size="sm" disabled={data.last} onClick={() => setPage(p => p + 1)}>
              Next
              <ChevronRight size={14} />
            </Button>
          </div>
        </div>
      )}
    </div>
  )
}
