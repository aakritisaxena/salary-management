import client from './client'
import type { Employee, EmployeeRequest, InsightsResponse, JobTitleInsight, PagedResponse } from '@/types'

interface ListParams {
  page?: number
  size?: number
  country?: string
  department?: string
}

export const employeeApi = {
  list: (params: ListParams = {}) =>
    client.get<PagedResponse<Employee>>('/api/employees', { params }).then(r => r.data),

  get: (id: string) =>
    client.get<Employee>(`/api/employees/${id}`).then(r => r.data),

  create: (data: EmployeeRequest) =>
    client.post<Employee>('/api/employees', data).then(r => r.data),

  update: (id: string, data: EmployeeRequest) =>
    client.put<Employee>(`/api/employees/${id}`, data).then(r => r.data),

  delete: (id: string) =>
    client.delete<{ message: string }>(`/api/employees/${id}`).then(r => r.data),
}

export const insightsApi = {
  getInsights: () =>
    client.get<InsightsResponse>('/api/insights').then(r => r.data),

  getJobTitleInsights: (country: string) =>
    client.get<JobTitleInsight[]>('/api/insights/job-titles', { params: { country } }).then(r => r.data),
}
