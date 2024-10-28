package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShareItTest {
    @Test
    public void givenArgumentAsConsoleInput_WhenReadFromSubstitutedByteArrayInputStream_ThenSuccessfullyCalculate() throws IOException {
        String[] arguments = new String[]{"-i", "CONSOLE"};

        try (MockedStatic<ShareItApp> mockedStatic = Mockito.mockStatic(ShareItApp.class, Mockito.CALLS_REAL_METHODS); InputStream fips = new ByteArrayInputStream("1 2 3".getBytes())) {

            InputStream original = System.in;
            System.setIn(fips);

            ArgumentCaptor<String[]> stringArgumentCaptor = ArgumentCaptor.forClass(String[].class);
            ShareItApp.main(arguments);

            mockedStatic.verify(() -> ShareItApp.main(stringArgumentCaptor.capture()));
            System.setIn(original);

            String[] capturedArgs = stringArgumentCaptor.getValue();
            assertEquals(2, capturedArgs.length);
            assertEquals("-i", capturedArgs[0]);
            assertEquals("CONSOLE", capturedArgs[1]);
        }
    }
}
