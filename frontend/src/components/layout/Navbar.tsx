import { NavLink } from 'react-router-dom'
import { BarChart3, Users } from 'lucide-react'
import { cn } from '@/lib/utils'

const links = [
  { to: '/', label: 'Employees', icon: Users, end: true },
  { to: '/insights', label: 'Insights', icon: BarChart3, end: false },
]

export default function Navbar() {
  return (
    <header className="border-b bg-card sticky top-0 z-10">
      <div className="max-w-7xl mx-auto px-6 h-14 flex items-center gap-8">
        <span className="font-semibold text-sm tracking-tight">Salary Management</span>
        <nav className="flex gap-1">
          {links.map(({ to, label, icon: Icon, end }) => (
            <NavLink
              key={to}
              to={to}
              end={end}
              className={({ isActive }) =>
                cn(
                  'flex items-center gap-2 px-3 py-1.5 rounded-md text-sm font-medium transition-colors',
                  isActive
                    ? 'bg-primary text-primary-foreground'
                    : 'text-muted-foreground hover:text-foreground hover:bg-muted'
                )
              }
            >
              <Icon size={15} />
              {label}
            </NavLink>
          ))}
        </nav>
      </div>
    </header>
  )
}
