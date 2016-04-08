package com.mccauley.driverlog.database;

import android.location.Location;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

public class TripTest {

    @Mock
    Location startLocation;
    @Mock
    Location endLocation;

    Trip testObject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(startLocation.getTime()).thenReturn(DateTime.now().minusHours(1).getMillis());
        when(endLocation.getTime()).thenReturn(DateTime.now().getMillis());
        testObject = new Trip(startLocation, endLocation);
    }

    @Test
    public void testDistance() throws Exception {
        when(startLocation.distanceTo(endLocation)).thenReturn(10000.0f);

        Double distance = testObject.distance();

        Assert.assertEquals(10000.0f/1609.344, distance);
    }

    @Test
    public void testDuration() throws Exception {
        Duration duration = testObject.duration();

        Assert.assertEquals(1, duration.getStandardHours());
    }
}