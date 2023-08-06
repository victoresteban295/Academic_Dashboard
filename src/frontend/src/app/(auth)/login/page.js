import LoginWidget from "@/components/auth/LoginWidget"
import { Box } from "@mui/material"

const login = () => {
    return (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
                mx: 2,
                maxWidth: '100%',
            }}
        >
            <LoginWidget />
        </Box>
    )
}

export default login
