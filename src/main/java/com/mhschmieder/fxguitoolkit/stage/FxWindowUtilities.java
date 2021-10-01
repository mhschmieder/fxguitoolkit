/**
 * MIT License
 *
 * Copyright (c) 2020, 2021 Mark Schmieder
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is part of the FxGuiToolkit Library
 *
 * You should have received a copy of the MIT License along with the
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.stage;

import java.util.List;
import java.util.prefs.Preferences;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * {@code FxWindowUtilities} is a static utility class for common window
 * functionality and sizing.
 */
public final class FxWindowUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private FxWindowUtilities() {}

    /**
     * If this window is a {@link Stage}, set Full Screen and Maximized Modes.
     *
     * @param window
     *            The window to set Maximized Modes (if a {@link Stage}).
     * @param fullScreenMode
     *            {@code true} if Full Screen Mode should be set;
     *            {@code false} otherwise
     * @param maximizedMode
     *            {@code true} if Maximized Mode should be set;
     *            {@code false} otherwise
     */
    public static void setWindowMaximizedModes( final Window window,
                                                final boolean fullScreenMode,
                                                final boolean maximizedMode ) {
        if ( window instanceof Stage ) {
            final Stage stage = ( ( Stage ) window );

            // Make sure we explicitly enter or exit Full Screen Mode.
            stage.setFullScreen( fullScreenMode );

            // Make sure we explicitly enter or exit Maximized Mode.
            stage.setMaximized( maximizedMode );
        }
    }

    /**
     * If this window is a {@link Stage}, exit Full Screen and Maximized Modes.
     *
     * @param window
     *            The window to exit Maximized Modes (if a {@link Stage}).
     */
    public static void exitWindowMaximizedModes( final Window window ) {
        setWindowMaximizedModes( window, false, false );
    }

    /**
     * Finds the screen where the enclosing {@link Rectangle2D} windowBounds
     * is located, and returns true if some parts of the associated window
     * are out of the screen, and therefore not visible.
     *
     * @param windowBounds
     *            The {@link Rectangle2D} to check for screen visibility
     * @return A flag for whether or not the associated window would be clipped
     *         or hidden due to appearing partially or fully on screens that
     *         are no longer available, plugged in, or turned on
     */
    public static boolean isWindowOutOfBounds( final Rectangle2D windowBounds ) {
        // Get the full list of screens that fully or partially contain the
        // provided window bounds.
        final double width = windowBounds.getWidth();
        final double height = windowBounds.getHeight();
        final double minX = windowBounds.getMinX();
        final double minY = windowBounds.getMinY();
        final List< Screen > screenList =
                                        Screen.getScreensForRectangle( minX, minY, width, height );

        // If the window is not even partially visible on any of the screens,
        // it is considered completely out of bounds.
        if ( screenList.size() < 1 ) {
            return true;
        }

        final double maxX = minX + width;
        final double maxY = minY + height;

        boolean upperLeftOutOfBounds = true;
        boolean upperRightOutOfBounds = true;
        boolean lowerLeftOutOfBounds = true;
        boolean lowerRightOutOfBounds = true;

        // Cycle through all of the candidate screens to make sure each of the
        // four corners of the provided window are within the bounds of one of
        // them, as otherwise we have partial clipping of the window.
        for ( final Screen screen : screenList ) {
            final Rectangle2D bounds = screen.getVisualBounds();

            if ( bounds.contains( minX, minY ) ) {
                upperLeftOutOfBounds = false;
            }

            if ( bounds.contains( maxX, minY ) ) {
                upperRightOutOfBounds = false;
            }

            if ( bounds.contains( minX, maxY ) ) {
                lowerLeftOutOfBounds = false;
            }

            if ( bounds.contains( maxX, maxY ) ) {
                lowerRightOutOfBounds = false;
            }
        }

        return upperLeftOutOfBounds || upperRightOutOfBounds || lowerLeftOutOfBounds
                || lowerRightOutOfBounds;
    }

    /**
     * Finds the screen where the {@link Window} is and returns true if some
     * parts of the window are out of the screen, and therefore not visible.
     *
     * @param window
     *            The {@link Window} to check for screen visibility
     * @return A flag for whether or not this window would be clipped or hidden
     *         due to appearing partially or fully on screens that are no longer
     *         available, plugged in, or turned on
     */
    public static boolean isWindowOutOfBounds( final Window window ) {
        final Rectangle2D windowBounds = new Rectangle2D( window.getX(),
                                                          window.getY(),
                                                          window.getWidth(),
                                                          window.getHeight() );

        return FxWindowUtilities.isWindowOutOfBounds( windowBounds );
    }

    public static void setWindowLocation( final Window window, final Point2D windowLocation ) {
        // Set the window's location on the screen (in pixels).
        window.setX( windowLocation.getX() );
        window.setY( windowLocation.getY() );
    }

    /**
     * This method sets the supplied window size as the new window size.
     *
     * @param window
     *            The window to resize
     * @param windowSize
     *            The window size to set as current window size
     */
    public static void setWindowSize( final Window window, final Dimension2D windowSize ) {
        // Set the new window size, but don't bother checking if the window is
        // resizable as we wouldn't have reached this method if the window
        // width and height change listeners are registered on a non-resizable
        // window. JavaFX takes care of managing these rules for us.
        window.setWidth( windowSize.getWidth() );
        window.setHeight( windowSize.getHeight() );
    }

    public static Point2D getAdjustedWindowLocation( final double windowX, final double windowY ) {
        // Get the full list of screens that are currently accessible.
        final List< Screen > screenList = Screen.getScreens();

        // If there are no accessible screens, don't try to set location.
        if ( screenList.size() < 1 ) {
            return null;
        }

        // Make sure the coordinates are within range of the full set of
        // available screens.
        boolean locationOutOfBounds = true;
        for ( final Screen screen : screenList ) {
            final Rectangle2D bounds = screen.getVisualBounds();

            if ( bounds.contains( windowX, windowY ) ) {
                locationOutOfBounds = false;
                break;
            }
        }

        // Adjust the window location if it is out of bounds.
        final Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        final double windowXAdjusted = locationOutOfBounds ? bounds.getMinX() : windowX;
        final double windowYAdjusted = locationOutOfBounds ? bounds.getMinY() : windowY;

        // Set and cache the window's location on the screen (in pixels).
        return new Point2D( windowXAdjusted, windowYAdjusted );
    }

    public static Dimension2D getAdjustedWindowSize( final double windowWidth,
                                                     final double windowHeight,
                                                     final Dimension2D defaultWindowSize,
                                                     final Dimension2D minimumWindowSize,
                                                     final Dimension2D maximumWindowSize ) {
        // To prevent Application startup issues with windows only having a
        // minimal Frame Title Bar and no Content Pane (due to exceptions or
        // other problems), we enforce minimum dimensions.
        double windowWidthAdjusted = ( windowWidth > minimumWindowSize.getWidth() )
            ? windowWidth
            : defaultWindowSize.getWidth();
        double windowHeightAdjusted = ( windowHeight > minimumWindowSize.getHeight() )
            ? windowHeight
            : defaultWindowSize.getHeight();

        // Likewise, as the Screen Size or resolution may have changed since the
        // previous session, we ensure that the Preferred Size can still fit.
        windowWidthAdjusted = Math.min( maximumWindowSize.getWidth(), windowWidthAdjusted );
        windowHeightAdjusted = Math.min( maximumWindowSize.getHeight(), windowHeightAdjusted );

        // Cache the window's adjusted Preferred Size on the Screen (in pixels).
        return new Dimension2D( windowWidthAdjusted, windowHeightAdjusted );
    }

    public static Dimension2D adjustWindowWithinBounds( final Window window,
                                                        final Dimension2D preferredWindowSize,
                                                        final Dimension2D minimumWindowSize,
                                                        final Dimension2D maximumWindowSize ) {
        // Unfortunately, the window may not be fully visible if it was last
        // saved on a different screen setup, so if it is fully or partially on
        // another screen, we need to re-center it on the main screen and adjust
        // the size if necessary.
        if ( !isWindowOutOfBounds( window ) ) {
            return preferredWindowSize;
        }

        // Get the compensated bounds for the primary screen. This is
        // guaranteed to account for things like the dock, application menu
        // bar, etc., regardless of which platform we are running on.
        final Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        // Adjust and cache the preferred size for this window.
        final double boundsWidth = bounds.getWidth();
        final double boundsHeight = bounds.getHeight();
        final double adjustedWidth = Math.min( window.getWidth(), boundsWidth );
        final double adjustedHeight = Math.min( window.getHeight(), boundsHeight );

        // Adjust and set the window's location on the screen (in pixels).
        final double adjustedX = bounds.getMinX() + ( 0.5d * ( boundsWidth - adjustedWidth ) );
        final double adjustedY = bounds.getMinY() + ( 0.5d * ( boundsHeight - adjustedHeight ) );
        final Point2D adjustedWindowLocation = new Point2D( adjustedX, adjustedY );
        setWindowLocation( window, adjustedWindowLocation );

        // Attempt to explicitly set the adjusted preferred window size.
        final Dimension2D adjustedWindowSize = getAdjustedWindowSize( adjustedWidth,
                                                                      adjustedHeight,
                                                                      preferredWindowSize,
                                                                      minimumWindowSize,
                                                                      maximumWindowSize );
        exitWindowMaximizedModes( window );
        setWindowSize( window, adjustedWindowSize );

        return adjustedWindowSize;
    }

    /**
     * This method saves the Window Layout Preferences for this window. It
     * starts by checking the current menu setting (if available), which is
     * saved for the next session. Only if "Preferred Size" is currently
     * selected, does it also store the current window size as a preference.
     *
     * @param prefs
     *            The {@link Preferences} reference for the key/value pairs
     * @param windowKeyPrefix
     *            The prefix to use for the window key lookup
     * @param window
     *            The Window whose layout should be saved
     * @param preferredWindowSize
     *            The preferred size for the supplied Window
     */
    @SuppressWarnings("nls")
    public static void saveWindowLayout( final Preferences prefs,
                                         final String windowKeyPrefix,
                                         final Window window,
                                         final Dimension2D preferredWindowSize ) {
        // If this window has no key prefix, it can't save preferences.
        if ( ( windowKeyPrefix == null ) || windowKeyPrefix.isEmpty() ) {
            return;
        }

        // Save the window's preferred layout location.
        final String windowXKey = windowKeyPrefix + "X";
        final double windowXValue = window.getX();
        prefs.putDouble( windowXKey, windowXValue );
        final String windowYKey = windowKeyPrefix + "Y";
        final double windowYValue = window.getY();
        prefs.putDouble( windowYKey, windowYValue );

        // Determine whether the user was in Full Screen Mode when they exited.
        final String fullScreenModeKey = windowKeyPrefix + "FullScreenMode";
        final boolean fullScreenMode =
                                     window instanceof Stage && ( ( Stage ) window ).isFullScreen();
        prefs.putBoolean( fullScreenModeKey, fullScreenMode );

        // Determine whether the user was in Maximized Mode when they exited.
        // :TODO: Figure out why maximized is always true for undecorated
        // stages.
        final String maximizedModeKey = windowKeyPrefix + "MaximizedMode";
        final boolean maximizedMode = window instanceof Stage && ( ( Stage ) window ).isMaximized();
        prefs.putBoolean( maximizedModeKey, maximizedMode );

        // Save the window's current size as the new preferred layout bounds,
        // unless in Full Screen Mode or Maximized Mode, where it is safer to
        // save the last cached Preferred Size instead, as Full Screen Mode Size
        // and Maximized Mode Size should only be modes and never used directly
        // (the OS knows best how to apply them).
        // :TODO: Figure out why maximized is always true for undecorated
        // stages.
        final String windowWidthKey = windowKeyPrefix + "Width";
        final double windowWidthValue = ( fullScreenMode ) // || maximizedMode )
            ? preferredWindowSize.getWidth()
            : window.getWidth();
        prefs.putDouble( windowWidthKey, windowWidthValue );
        final String windowHeightKey = windowKeyPrefix + "Height";
        final double windowHeightValue = ( fullScreenMode ) // || maximizedMode
                                                            // )
            ? preferredWindowSize.getHeight()
            : window.getHeight();
        prefs.putDouble( windowHeightKey, windowHeightValue );
    }

    /**
     * This method restores the Window Layout Preferences for this window. It
     * starts by checking the desired menu setting (if available), which is
     * saved from the previous session. Only if "Preferred Size" is desired,
     * does it also restore the cached window size from preferences.
     * <p>
     * Generally this method is only called at the application level, so this
     * override takes care of invoking it on all windows owned by the
     * application.
     *
     * @param prefs
     *            The {@link Preferences} reference for the key/value pairs
     * @param windowKeyPrefix
     *            The prefix to use for the window key lookup
     * @param window
     *            The Window whose layout should be saved
     * @param defaultWindowSize
     *            The default size for the supplied Window
     * @param minimumWindowSize
     *            The minimum size for the supplied Window
     * @param maximumWindowSize
     *            The maximum size for the supplied Window
     * @return The adjusted and bounded window size to use as the new
     *         preferred size, or {@code null} if overruled by maximized modes
     */
    @SuppressWarnings("nls")
    public static Dimension2D restoreWindowLayout( final Preferences prefs,
                                                   final String windowKeyPrefix,
                                                   final Window window,
                                                   final Dimension2D defaultWindowSize,
                                                   final Dimension2D minimumWindowSize,
                                                   final Dimension2D maximumWindowSize ) {
        // If this window has no key prefix, it can't restore preferences.
        if ( ( windowKeyPrefix == null ) || windowKeyPrefix.isEmpty() ) {
            // Attempt to set the default window size.
            exitWindowMaximizedModes( window );
            setWindowSize( window, defaultWindowSize );
            return defaultWindowSize;
        }

        // Determine whether the user was in Full Screen Mode when they exited.
        final String fullScreenModeKey = windowKeyPrefix + "FullScreenMode";
        final boolean fullScreenMode = prefs.getBoolean( fullScreenModeKey, false );

        // Determine whether the user was in Maximized Mode when they exited.
        // :TODO: Figure out why this flag is always true for undecorated
        // stages.
        final String maximizedModeKey = windowKeyPrefix + "MaximizedMode";
        final boolean maximizedMode = false; // prefs.getBoolean(
                                             // maximizedModeKey, false );

        // It is important to exit window maximized modes before setting the
        // restored location and size preferences.
        setWindowMaximizedModes( window, fullScreenMode, maximizedMode );

        // If Full Screen or Maximized Mode, exit early, as we shouldn't try
        // to set location or size for explicit window maximized modes.
        if ( fullScreenMode || maximizedMode ) {
            return null;
        }

        // Restore the window's cached layout location from the last session.
        // :NOTE: Default location accounts for issues with corner areas on some
        // OS versions, but may be too much for smaller screens.
        final String windowXKey = windowKeyPrefix + "X";
        final double windowXValue = prefs.getDouble( windowXKey, 100d );
        final String windowYKey = windowKeyPrefix + "Y";
        final double windowYValue = prefs.getDouble( windowYKey, 100d );
        final Point2D adjustedWindowLocation = getAdjustedWindowLocation( windowXValue,
                                                                          windowYValue );
        if ( adjustedWindowLocation != null ) {
            setWindowLocation( window, adjustedWindowLocation );
        }

        // Restore the preferred window size from the last session.
        final String windowWidthKey = windowKeyPrefix + "Width";
        final double windowWidth = prefs.getDouble( windowWidthKey, defaultWindowSize.getWidth() );
        final String windowHeightKey = windowKeyPrefix + "Height";
        final double windowHeight =
                                  prefs.getDouble( windowHeightKey, defaultWindowSize.getHeight() );
        final Dimension2D adjustedWindowSize = getAdjustedWindowSize( windowWidth,
                                                                      windowHeight,
                                                                      defaultWindowSize,
                                                                      minimumWindowSize,
                                                                      maximumWindowSize );

        // Attempt to explicitly set the adjusted preferred window size.
        setWindowSize( window, adjustedWindowSize );

        // If necessary, adjust this adjusted window size to be within bounds.
        return adjustWindowWithinBounds( window,
                                         adjustedWindowSize,
                                         minimumWindowSize,
                                         maximumWindowSize );
    }

}
