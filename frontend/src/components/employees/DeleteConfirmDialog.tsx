import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
import type { Employee } from '@/types'

interface Props {
  employee: Employee | null
  onClose: () => void
  onConfirm: () => void
  isLoading?: boolean
}

export default function DeleteConfirmDialog({ employee, onClose, onConfirm, isLoading }: Props) {
  return (
    <AlertDialog open={!!employee} onOpenChange={(v) => !v && onClose()}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Archive {employee?.fullName}?</AlertDialogTitle>
          <AlertDialogDescription>
            This will archive the employee record. The data will be retained in history but
            the employee will no longer appear in active listings.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel onClick={onClose}>Cancel</AlertDialogCancel>
          <AlertDialogAction
            onClick={onConfirm}
            disabled={isLoading}
            className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
          >
            {isLoading ? 'Archiving…' : 'Archive'}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  )
}
