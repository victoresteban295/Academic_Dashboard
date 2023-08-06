import RegisterWidget from "@/components/auth/register/RegisterWidget";
import { Box } from "@mui/material";

const register = () => {
    return (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'row',
                justifyContent: 'center',
                alignItems: 'center',
                mx: 2,
                maxWidth: '100%',
            }}
        >
            <Box
            sx={{
                maxWidth: '750px',
                flexGrow: 1,
            }}
            >
                <RegisterWidget />
            </Box>
        </Box>
    )
}

export default register; 
