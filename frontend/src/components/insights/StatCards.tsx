import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

interface Stat {
  label: string
  value: string
}

interface Props {
  stats: Stat[]
}

export default function StatCards({ stats }: Props) {
  return (
    <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
      {stats.map(({ label, value }) => (
        <Card key={label}>
          <CardHeader className="pb-1">
            <CardTitle className="text-sm font-medium text-muted-foreground">{label}</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-2xl font-semibold tabular-nums">{value}</p>
          </CardContent>
        </Card>
      ))}
    </div>
  )
}
