export interface Employee {
  id: string
  fullName: string
  jobTitle: string
  country: string
  salary: number
  currency: string
  email: string
  department: string
  hireDate: string
  createdAt: string
  updatedAt: string
}

export interface EmployeeRequest {
  fullName: string
  jobTitle: string
  country: string
  salary: number
  currency: string
  email: string
  department: string
  hireDate: string
}

export interface PagedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  numberOfElements: number
  first: boolean
  last: boolean
  pageable: {
    pageNumber: number
    pageSize: number
  }
}

export interface DepartmentInsight {
  department: string
  headcount: number
  averageSalary: number
  minSalary: number
  maxSalary: number
}

export interface CountryInsight {
  country: string
  headcount: number
  averageSalary: number
  minSalary: number
  maxSalary: number
}

export interface JobTitleInsight {
  jobTitle: string
  headcount: number
  averageSalary: number
  minSalary: number
  maxSalary: number
}

export interface InsightsResponse {
  totalEmployees: number
  averageSalary: number
  minSalary: number
  maxSalary: number
  byDepartment: DepartmentInsight[]
  byCountry: CountryInsight[]
}
