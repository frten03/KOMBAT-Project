import "../styles/layout.css"

interface Props {
    children: React.ReactNode
}

export default function AppLayout({ children }: Props) {
    return (
        <div className="app-layout">
            {children}
        </div>
    )
}