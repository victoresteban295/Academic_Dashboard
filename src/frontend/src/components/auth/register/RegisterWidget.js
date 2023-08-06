"use client"
import * as React from 'react';
import Box from '@mui/material/Box';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import LoginWidget from '../LoginWidget';
import AcademicForm from './AcademicForm';
import ProfileForm from './ProfileForm';

const steps = ['Academic Institution', 'Profile', 'Review'];

const RegisterWidget = () => {

    const [activeStep, setActiveStep] = React.useState(0);
    const [skipped, setSkipped] = React.useState(new Set());

    /* Academic Institution Form Values*/
    const [profileType, setProfileType] = React.useState('');

    const handleProfileType = (profileType) => {
        setProfileType(profileType);
    };

    const isStepSkipped = (step) => {
        return skipped.has(step);
    };

    const handleNext = () => {
        let newSkipped = skipped;
        if (isStepSkipped(activeStep)) {
            newSkipped = new Set(newSkipped.values());
            newSkipped.delete(activeStep);
        }

        setActiveStep((prevActiveStep) => prevActiveStep + 1);
        setSkipped(newSkipped);
    };

    const handleBack = () => {
        setActiveStep((prevActiveStep) => prevActiveStep - 1);
    };

    const handleReset = () => {
        setActiveStep(0);
    };

    return (
        <Box 
            sx={{ 
                p: 4,
                bgcolor: 'widget.background',
                boxShadow: '1px 1px 4px 2px #cecece',
                borderRadius: '10px',
                width: '100%',
                my: 8,
            }}
        >
            <Stepper activeStep={activeStep}>
                {steps.map((label, index) => {
                    return (
                        <Step key={label} >
                            <StepLabel>{label}</StepLabel>
                        </Step>
                    );
                })}
            </Stepper>
            {(activeStep === 0) ? (
                <React.Fragment> 
                    <AcademicForm 
                        profileType={profileType}
                        selectProfile={handleProfileType}
                    />
                    <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>
                        <Box sx={{ flex: '1 1 auto' }} /> 
                        <Button variant="contained" onClick={handleNext}> 
                            <Typography
                                variant="button"
                                sx={{
                                    color: '#000',
                                    fontWeight: '700',
                                }}
                            >
                                Next
                            </Typography>
                        </Button> 
                    </Box> 
                </React.Fragment>
            ) : (activeStep === 1) ? (
                <React.Fragment> 
                    <ProfileForm />
                    {(profileType === "STUDENT") ? (
                        <Typography sx={{ mt: 2, mb: 1 }}>Student Form</Typography>
                    ) : (
                        <Typography sx={{ mt: 2, mb: 1 }}>Professor Form</Typography>
                    )} 
                    <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>
                        <Button 
                            disabled={activeStep === 0}
                            variant="contained"
                            onClick={handleBack}
                            sx={{ mr: 1 }}
                        > 
                            <Typography
                                variant="button"
                                sx={{
                                    color: '#000',
                                    fontWeight: '700',
                                }}
                            >
                                Back
                            </Typography>
                        </Button> 
                        <Box sx={{ flex: '1 1 auto' }} /> 
                        <Button variant="contained" onClick={handleNext}> 
                            <Typography
                                variant="button"
                                sx={{
                                    color: '#000',
                                    fontWeight: '700',
                                }}
                            >
                                Next
                            </Typography>
                        </Button> 
                    </Box> 
                </React.Fragment>
            ) : (
                <React.Fragment>
                    <LoginWidget />
                    <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>
                        <Button 
                            disabled={activeStep === 0}
                            variant="contained"
                            onClick={handleBack}
                            sx={{ mr: 1 }}
                        > 
                            <Typography
                                variant="button"
                                sx={{
                                    color: '#000',
                                    fontWeight: '700',
                                }}
                            >
                                Back
                            </Typography>
                        </Button> 
                        <Box sx={{ flex: '1 1 auto' }} />
                        <Button 
                            variant="contained"
                            onClick={handleReset}
                        > 
                            <Typography
                                variant="button"
                                sx={{
                                    color: '#000',
                                    fontWeight: '700',
                                }}
                            >
                                Submit
                            </Typography>
                        </Button> 
                    </Box>
                </React.Fragment>
            )}
        </Box>
    );
}

export default RegisterWidget;
