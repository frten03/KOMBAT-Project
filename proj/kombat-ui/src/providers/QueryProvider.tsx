import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactNode } from 'react'

const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            staleTime: 1000 * 5,
            gcTime: 1000 * 60 * 5,
            retry: 2,
            refetchOnWindowFocus: false,
        },
    },
})

type Props = {
    children: ReactNode
}

export function QueryProvider({ children }: Props) {
    return (
        <QueryClientProvider client={queryClient}>
            {children}
        </QueryClientProvider>
    )
}
export { queryClient }
