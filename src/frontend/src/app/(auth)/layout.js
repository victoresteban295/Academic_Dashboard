import Footer from '@/components/Footer';
import ThemeRegistry from '@/theme/ThemeRegistry'
import { AppBar, Avatar, Box, Divider, Toolbar, Typography } from "@mui/material"
import { Teko } from "next/font/google"

const teko700 = Teko({
    weight: '700',
    subsets: ['latin'],
})

const loginLayout = ({ children }) => {
    return (
        <html lang="eng">
            <body>
                <ThemeRegistry options={{ key: 'mui'}}>
                    <Box sx={{position:'sticky', top:'0px', zIndex:'999',}}>
                        <AppBar position='sticky'>
                            <Toolbar disableGutters>
                            </Toolbar>
                        </AppBar>
                    </Box>
                    {children}
                    <Footer />
                </ThemeRegistry>
            </body>
        </html>
    )
};

export default loginLayout;
