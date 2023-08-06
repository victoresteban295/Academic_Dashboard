"use client"

import * as React from 'react';
import { Box, Stack, Button, Divider, TextField, Typography, FormControl, InputLabel, OutlinedInput, InputAdornment, IconButton } from "@mui/material"
import { Teko } from "next/font/google"
import Image from "next/image";
import { Visibility, VisibilityOff } from '@mui/icons-material';
import Link from 'next/link';

const teko = Teko({
    weight: '700',
    subsets: ['latin'],
})

const LoginWidget = () => {
    const [showPassword, setShowPassword] = React.useState(false);

    const handleClickShowPassword = () => setShowPassword((show) => !show);

    const handleMouseDownPassword = (event) => {
        event.preventDefault();
    };

    return (
        <Stack 
            spacing={1}
            sx ={{
                p: 4,
                bgcolor: 'widget.background',
                boxShadow: '1px 1px 4px 2px #cecece',
                borderRadius: '10px',
                maxWidth: '100%',
                my: 8,
            }}
        >
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                }}
            >
                <Image 
                    src="/images/school.png"
                    height={100}
                    width={100}
                    alt="Picture of academic building"
                />
            </Box>
            <Box
                sx={{
                    display: 'flex',
                    flexDirection: 'row',
                    justifyContent: 'center',
                    alignItems: 'center',
                }}
            >
                <Typography
                    variant='h4'
                    align='center'
                    sx={{
                        fontFamily: teko.style.fontFamily,
                    }}
                >
                    Academic Dashboard
                </Typography>
            </Box>
            <TextField label="Username" variant="outlined" />
            <FormControl sx={{ m: 1 }} variant="outlined">
                <InputLabel htmlFor="outlined-adornment-password">Password</InputLabel>
                <OutlinedInput
                    id="outlined-adornment-password"
                    type={showPassword ? 'text' : 'password'}
                    endAdornment={
                        <InputAdornment position="end">
                            <IconButton
                                aria-label="toggle password visibility"
                                onClick={handleClickShowPassword}
                                onMouseDown={handleMouseDownPassword}
                                edge="end"
                            >
                                {showPassword ? <VisibilityOff /> : <Visibility />}
                            </IconButton>
                        </InputAdornment>
                    }
                    label="Password"
                />
            </FormControl>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                }}
            >
                <Button variant="contained">
                    <Typography 
                        sx={{
                            color: '#000',
                            fontWeight: '700',
                        }}
                        variant="button">
                        Login
                    </Typography>
                </Button>
            </Box>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                }}
            >
                <Button variant="text">
                    <Typography 
                        sx={{
                            color: '#000',
                            fontWeight: '700',
                        }}
                        variant="caption">
                        Forgot Password?
                    </Typography>
                </Button>
            </Box>
            <Divider variant="middle" />
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                }}
            >
                <Link href="/register" style={{textDecoration: 'none'}} >
                    <Button variant="contained">
                        <Typography 
                            sx={{
                                color: '#000',
                                fontWeight: '700',
                            }}
                            variant="button">
                            Create New Account
                        </Typography>
                    </Button>
                </Link>
            </Box>
        </Stack> 
    )
};

export default LoginWidget;
