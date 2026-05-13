import { useState } from 'react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { employeeApi } from '@/api/employees'
import type { Employee, EmployeeRequest } from '@/types'
import EmployeeTable from '@/components/employees/EmployeeTable'
import EmployeeForm from '@/components/employees/EmployeeForm'
import DeleteConfirmDialog from '@/components/employees/DeleteConfirmDialog'

export default function EmployeesPage() {
  const qc = useQueryClient()
  const [formOpen, setFormOpen] = useState(false)
  const [editing, setEditing] = useState<Employee | null>(null)
  const [deleting, setDeleting] = useState<Employee | null>(null)

  const createMutation = useMutation({
    mutationFn: (req: EmployeeRequest) => employeeApi.create(req),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['employees'] })
      setFormOpen(false)
      toast.success('Employee created')
    },
    onError: (e: unknown) => {
      const detail = (e as { response?: { data?: { detail?: string } } })
        ?.response?.data?.detail
      toast.error(detail ?? 'Failed to create employee')
    },
  })

  const updateMutation = useMutation({
    mutationFn: ({ id, req }: { id: string; req: EmployeeRequest }) =>
      employeeApi.update(id, req),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['employees'] })
      setEditing(null)
      setFormOpen(false)
      toast.success('Employee updated')
    },
    onError: (e: unknown) => {
      const detail = (e as { response?: { data?: { detail?: string } } })
        ?.response?.data?.detail
      toast.error(detail ?? 'Failed to update employee')
    },
  })

  const deleteMutation = useMutation({
    mutationFn: (id: string) => employeeApi.delete(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['employees'] })
      setDeleting(null)
      toast.success('Employee archived')
    },
    onError: () => toast.error('Failed to archive employee'),
  })

  function handleSubmit(req: EmployeeRequest) {
    if (editing) {
      updateMutation.mutate({ id: editing.id, req })
    } else {
      createMutation.mutate(req)
    }
  }

  function handleCloseForm() {
    setFormOpen(false)
    setEditing(null)
  }

  return (
    <>
      <EmployeeTable
        onAdd={() => { setEditing(null); setFormOpen(true) }}
        onEdit={(emp) => { setEditing(emp); setFormOpen(true) }}
        onDelete={(emp) => setDeleting(emp)}
      />

      <EmployeeForm
        open={formOpen}
        employee={editing}
        onClose={handleCloseForm}
        onSubmit={handleSubmit}
        isLoading={createMutation.isPending || updateMutation.isPending}
      />

      <DeleteConfirmDialog
        employee={deleting}
        onClose={() => setDeleting(null)}
        onConfirm={() => deleting && deleteMutation.mutate(deleting.id)}
        isLoading={deleteMutation.isPending}
      />
    </>
  )
}
