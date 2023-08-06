import { AppBar, Avatar, Box, Toolbar, Typography } from "@mui/material"
import { Teko } from "next/font/google"

const teko700 = Teko({
    weight: '700',
    subsets: ['latin'],
})

const Navbar = () => {
    return (
        <Box sx={{position:'sticky', top:'0px', zIndex:'999',}}>
            <AppBar position='sticky'>
                <Toolbar disableGutters>
                        <Box
                            sx={{
                                display: 'flex',
                                alignItems: 'center',
                                postion: 'static',
                                flexGrow: 1,
                                mx: 3,
                            }}
                        >
                            <Typography 
                                variant='h5'
                                sx={{
                                    mx: 1,
                                    fontFamily: teko700.style.fontFamily
                                }}
                            >
                                Academic Dashboard
                            </Typography> 
                        </Box>
                        <Avatar 
                            sx={{
                                bgcolor: 'grey', 
                                mx: 3,
                                display: 'flex',
                                justifyContent: 'center',
                                alignItems: 'center',
                            }}
                        >
                            <Typography 
                                variant='h6'
                                sx={{
                                    p: 0,
                                    fontFamily: teko700.style.fontFamily
                                }}
                            >
                                VE
                            </Typography> 
                        </Avatar>
                </Toolbar>
            </AppBar>
        </Box>
    )
}

export default Navbar
