import "../styles/layout.css"

interface Props {
    children: React.ReactNode
}

export default function CenteredLayout({ children }: Props) {
    return (
        <div className="centered-layout">
            {children}
        </div>
    )
}