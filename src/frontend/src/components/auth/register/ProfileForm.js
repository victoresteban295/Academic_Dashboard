import { Box, Divider, Stack, TextField, Typography } from "@mui/material";

const ProfileForm = () => {
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
                        Personal Information
                    </Typography>
                    <Divider />
                </Stack>
                <Stack
                    direction={{xs: "column", sm: "row"}}
                    spacing={2}
                    useFlexGap
                >
                    <TextField id="first_name" label="First Name" variant="outlined" />
                    <TextField id="middle_name" label="Middle Name" variant="outlined" />
                    <TextField id="last_name" label="Last Name" variant="outlined" />
                </Stack>
                <Stack
                    spacing={1}
                >
                    <Typography
                        variant="body2"
                    >
                        Birthday
                    </Typography>
                    <Stack
                        direction={{xs: "column", sm: "row"}}
                        spacing={2}
                        useFlexGap
                    >
                        <TextField id="birth_month"  variant="outlined" />
                        <TextField id="birth_day"  variant="outlined" />
                        <TextField id="birth_year"  variant="outlined" />
                    </Stack>
                </Stack>
                <TextField id="academic_institution" label="Academic Institution" variant="outlined" />
                <TextField id="identification_code" label="Identification Code" variant="outlined" />
            </Stack>
        </Box>

   ) 
}

export default ProfileForm;
