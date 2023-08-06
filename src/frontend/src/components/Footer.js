import { Box, Divider, Stack, Typography } from "@mui/material";
import Link from "next/link";

const Footer = () => {
    return (
        <Box>
            <Typography align="center" variant="subtitle1">
                © 2023 Victor Esteban. All Right Reserved 
            </Typography>
            <Stack
                direction= 'row'
                divider = <Divider orientation="vertical" flexItem />
                spacing={2}
                justifyContent='center'
                sx={{
                    my: 1
                }}
            >
                <Link href="/" >
                    <Typography
                        sx={{
                            color: 'text.primary',
                        }}
                    >
                        Portfolio
                    </Typography>
                </Link>
                <Link href="https://github.com/victoresteban295" >
                    <Typography
                        sx={{
                            color: 'text.primary',
                        }}
                    >
                        GitHub
                    </Typography>
                </Link>
                <Link href="https://www.linkedin.com/in/victor-esteban295/" >
                    <Typography
                        sx={{
                            color: 'text.primary',
                        }}
                    >
                        LinkedIn
                    </Typography>
                </Link>
            </Stack>
        </Box>
    )
}

export default Footer;
