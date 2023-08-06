import { Box, Typography } from "@mui/material"
import { Cabin } from "next/font/google"

const cabin400 = Cabin({
    weight: '400',
    subsets: ['latin']
})

const cabin700 = Cabin({
    weight: '700',
    subsets: ['latin']
})

const Home = () => {
    return (
        <Box
            sx={{
                mx:2
            }}
        >
            <h1 className={cabin400.className}>Academic Dashboard</h1>
            <Typography
                variant='h6'
                sx = {{
                    fontFamily: cabin400.style.fontFamily
                }}
            >
                Academic Labs h6
            </Typography>
            <Typography
                variant='h6'
                sx = {{
                    fontFamily: cabin700.style.fontFamily
                }}
            >
                Academic Labs h6
            </Typography>
            <Typography
                variant='h6'
                sx = {{
                    fontFamily: cabin400.style.fontFamily
                }}
            >
                Academic Labs h6 400
            </Typography>
            <Typography
                variant='h6'
                sx = {{
                    fontFamily: cabin700.style.fontFamily
                }}
            >
                Academic Labs h6 700
            </Typography>
            <Typography
                variant='h6'
                sx = {{
                    fontWeight: '400'
                }}
            >
                Login Page
            </Typography>
            <Typography
                variant='body1'
                sx = {{
                    fontWeight: '700'
                }}
            >
                Academic Labs body1 700
            </Typography>
        </Box>
    )
}

export default Home
