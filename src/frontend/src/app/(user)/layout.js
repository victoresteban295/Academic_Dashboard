import ThemeRegistry from '@/theme/ThemeRegistry'
import Navbar from '@/components/Navbar'
import './globals.css'

export default function RootLayout({ children }) {
    return (
        <html lang="en">
            <body>
                <ThemeRegistry options={{ key: 'mui'}}>
                    <Navbar />
                    {children}
                </ThemeRegistry>
            </body>
        </html>
    )
}
