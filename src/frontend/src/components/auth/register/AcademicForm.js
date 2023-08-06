"use client"
import * as React from 'react';
import { Box, Divider, FormControl, InputLabel, MenuItem, Select, Stack, TextField, Typography } from "@mui/material";

const AcademicForm = (props) => {

    const handleChange = (event) => {
        props.selectProfile(event.target.value);
    };

    return (
        <Box
            sx={{
                display: 'flex',
                justifyContent: 'center',
                my: 5,
            }}
        >
            <Stack
                spacing={2}
                sx={{
                    maxWidth: '500px',
                    flexGrow: 1,
                }}
            >
                <Stack
                    spacing={0}
                    sx={{
                        width: '100%',
                        flexGrow: 1,
                    }}
                >
                    <Typography
                        variant='h5'
                        sx={{
                            fontWeight: "700",
                        }}
                    >
                        Academic Institution
                    </Typography>
                    <Divider />
                </Stack>
                <Box sx={{ minWidth: 120 }}>
                    <FormControl fullWidth>
                        <InputLabel id="select-profile">Profile Type</InputLabel>
                        <Select
                            labelId="select-profile"
                            id="select"
                            value={props.profileType}
                            label="Profile Type"
                            onChange={handleChange}
                        >
                            <MenuItem value={"STUDENT"}>Student</MenuItem>
                            <MenuItem value={"PROFESSOR"}>Professor</MenuItem>
                        </Select>
                    </FormControl>
                </Box>
                <TextField id="academic_institution" label="Academic Institution" variant="outlined" />
                <TextField id="identification_code" label="Identification Code" variant="outlined" />
            </Stack>
        </Box>
    )
}

export default AcademicForm;
