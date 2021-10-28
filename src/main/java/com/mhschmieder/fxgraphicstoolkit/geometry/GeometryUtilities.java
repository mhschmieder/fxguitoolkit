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
package com.mhschmieder.fxgraphicstoolkit.geometry;

import com.mhschmieder.commonstoolkit.math.Axis;
import com.mhschmieder.commonstoolkit.math.MathExt;
import com.mhschmieder.commonstoolkit.math.OrthogonalAxes;
import com.mhschmieder.commonstoolkit.physics.DistanceUnit;
import com.mhschmieder.commonstoolkit.physics.UnitConversion;
import com.mhschmieder.fxgraphicstoolkit.graphics.Extents2D;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.VLineTo;
import javafx.scene.transform.Affine;
import javafx.util.Pair;

/**
 * This utility class is mostly for converting between JavaFX and AWT graphics
 * classes, such as points. It does not have state.
 *
 * Functions that say "since 1.2" (e.g.) were ported from AWT source code, and
 * may have JavaFX equivalents that we simply haven't discovered yet.
 */
public final class GeometryUtilities {

    // NOTE: The constructor is disabled, as this is a static utilities class.
    private GeometryUtilities() {}

    /**
     * The bitmask that indicates that a point lies to the left of a
     * {@link Rectangle2D}.
     *
     * @since 1.2
     */
    public static final int    OUT_LEFT            = 1;

    /**
     * The bitmask that indicates that a point lies above a {@link Rectangle2D}.
     *
     * @since 1.2
     */
    public static final int    OUT_TOP             = 2;

    /**
     * The bitmask that indicates that a point lies to the right of a
     * {@link Rectangle2D}.
     *
     * @since 1.2
     */
    public static final int    OUT_RIGHT           = 4;

    /**
     * The bitmask that indicates that a point lies below a {@link Rectangle2D}.
     *
     * @since 1.2
     */
    public static final int    OUT_BOTTOM          = 8;

    /**
     * What <a href=
     * "https://encrypted.google.com/search?hl=en&q=1%20points%20in%20meters">
     * google said</a>.
     */
    public static final double ONE_POINT_IN_METERS = 0.000352778;

    public static BoundingBox boundsFromExtents( final Extents2D extents ) {
        return new BoundingBox( extents.getX(),
                                extents.getY(),
                                extents.getWidth(),
                                extents.getHeight() );
    }

    public static BoundingBox boundsFromRectangle( final Rectangle rectangle ) {
        return new BoundingBox( rectangle.getX(),
                                rectangle.getY(),
                                rectangle.getWidth(),
                                rectangle.getHeight() );
    }

    public static BoundingBox boundsFromRectangle2D( final Rectangle2D rectangle ) {
        return new BoundingBox( rectangle.getMinX(),
                                rectangle.getMinY(),
                                rectangle.getWidth(),
                                rectangle.getHeight() );
    }

    /*
     * NOTE: This is a unitless method, but does assume the units are at
     * least consistent. Preferably everything is metric (meters).
     */
    public static boolean contains( final Bounds bounds,
                                    final Point2D point,
                                    final boolean useFuzzyEq ) {
        if ( useFuzzyEq ) {
            // Add a fudge factor to account for floating point imprecision.
            final double fudgeFactor = 0.02d * Math.max( bounds.getWidth(), bounds.getHeight() );
            final Rectangle2D rect2 = new Rectangle2D( bounds.getMinX() - fudgeFactor,
                                                       bounds.getMinY() - fudgeFactor,
                                                       bounds.getWidth() + fudgeFactor,
                                                       bounds.getHeight() + fudgeFactor );
            return rect2.contains( point );
        }

        return bounds.contains( point );
    }

    public static boolean contains( final Rectangle2D area, final Line line ) {
        final Point2D p1 = new Point2D( line.getStartX(), line.getStartY() );
        final Point2D p2 = new Point2D( line.getEndX(), line.getEndY() );

        return area.contains( p1 ) || area.contains( p2 );
    }

    public static Point2D copyPoint2D( final Point2D point2D ) {
        final Point2D copiedPoint2D = new Point2D( point2D.getX(), point2D.getY() );
        return copiedPoint2D;
    }

    public static Point3D copyPoint3D( final Point3D point3D ) {
        final Point3D copiedPoint3D = new Point3D( point3D.getX(), point3D.getY(), point3D.getZ() );
        return copiedPoint3D;
    }

    /**
     * Returns the distance between two points.
     *
     * @param x1
     *            the X coordinate of the first specified point
     * @param y1
     *            the Y coordinate of the first specified point
     * @param x2
     *            the X coordinate of the second specified point
     * @param y2
     *            the Y coordinate of the second specified point
     * @return the distance between the two sets of specified coordinates.
     * @since 1.2
     */
    public static double distance( final double x1,
                                   final double y1,
                                   final double x2,
                                   final double y2 ) {
        final double x1Adjusted = x1 - x2;
        final double y1Adjusted = y1 - y2;
        return Math.hypot( x1Adjusted, y1Adjusted );
    }

    /**
     * Returns the distance from this <code>Point2D</code> to a specified point.
     *
     * @param pt
     *            The point from which to measure distance
     * @param px
     *            the X coordinate of the specified point to be measured against
     *            this <code>Point2D</code>
     * @param py
     *            the Y coordinate of the specified point to be measured against
     *            this <code>Point2D</code>
     * @return the distance between this <code>Point2D</code> and a specified
     *         point.
     * @since 1.2
     */
    public static double distance( final Point2D pt, final double px, final double py ) {
        final double pxAdjusted = px - pt.getX();
        final double pyAdjusted = py - pt.getY();
        return Math.hypot( pxAdjusted, pyAdjusted );
    }

    /**
     * Returns the square of the distance between two points.
     *
     * @param x1
     *            the X coordinate of the first specified point
     * @param y1
     *            the Y coordinate of the first specified point
     * @param x2
     *            the X coordinate of the second specified point
     * @param y2
     *            the Y coordinate of the second specified point
     * @return the square of the distance between the two sets of specified
     *         coordinates.
     * @since 1.2
     */
    public static double distanceSq( final double x1,
                                     final double y1,
                                     final double x2,
                                     final double y2 ) {
        final double x1Adjusted = x1 - x2;
        final double y1Adjusted = y1 - y2;
        return ( ( x1Adjusted * x1Adjusted ) + ( y1Adjusted * y1Adjusted ) );
    }

    /**
     * Returns the square of the distance from this <code>Point2D</code> to a
     * specified point.
     *
     * @param point
     *            The point from which to measure distance
     * @param px
     *            the X coordinate of the specified point to be measured against
     *            this <code>Point2D</code>
     * @param py
     *            the Y coordinate of the specified point to be measured against
     *            this <code>Point2D</code>
     * @return the square of the distance between this <code>Point2D</code> and
     *         the specified point.
     * @since 1.2
     */
    public static double distanceSq( final Point2D point, final double px, final double py ) {
        final double pxAdjusted = px - point.getX();
        final double pyAdjusted = py - point.getY();
        return ( ( pxAdjusted * pxAdjusted ) + ( pyAdjusted * pyAdjusted ) );
    }

    /**
     * Returns the square of the distance from one <code>Point2D</code> to
     * another specified <code>Point2D</code>.
     *
     * @param pt1
     *            The reference point to use for measuring another point
     * @param pt2
     *            The specified point to be measured against the reference point
     * @return the square of the distance between this <code>Point2D</code> to a
     *         specified <code>Point2D</code>.
     * @since 1.2
     */
    public static double distanceSq( final Point2D pt1, final Point2D pt2 ) {
        final double px = pt2.getX() - pt1.getX();
        final double py = pt2.getY() - pt1.getY();
        return ( ( px * px ) + ( py * py ) );
    }

    public static Point3D exchangeCoordinates( final Point3D point3D,
                                               final OrthogonalAxes orthogonalAxes ) {
        switch ( orthogonalAxes ) {
        case XY:
            return new Point3D( point3D.getY(), point3D.getX(), point3D.getZ() );
        case XZ:
            return new Point3D( point3D.getZ(), point3D.getY(), point3D.getX() );
        case YZ:
            return new Point3D( point3D.getX(), point3D.getZ(), point3D.getY() );
        default:
            return Point3D.ZERO;
        }
    }

    /**
     * This method takes a provided Path and iterates its control points and
     * vertices until it finds the lowest point in the context of the
     * transformed coordinates for the target space. The latter is specified as
     * a point and angle.
     *
     * @param path
     *            The path to iterate for finding lowest point
     * @param referencePoint
     *            The reference point for translating coordinate spaces
     * @param theta
     *            The reference angle, in radians, for transforming coordinate
     *            spaces
     * @return The lowest point as a pair of double-precision coordinates
     */
    public static Pair< Double, Double > findLowestPoint( final Path path,
                                                          final Point2D referencePoint,
                                                          final double theta ) {
        // Loop through the points that define the given path, treating each
        // path element type independently so that we don't miss any control
        // points on curved surfaces. Adjust for angular and translational
        // offsets also, in that order.
        final double offsetX = referencePoint.getX();
        final double offsetY = referencePoint.getY();
        double lowestX = offsetX;
        double lowestY = offsetY;
        double currentX = 0d;
        double currentY = 0d;

        for ( final PathElement pathElement : path.getElements() ) {
            if ( pathElement instanceof MoveTo ) {
                final MoveTo mt = ( MoveTo ) pathElement;

                currentX = mt.getX();
                currentY = mt.getY();

                final double mtX = transformX( currentX, currentY, offsetX, theta );
                final double mtY = transformY( currentX, currentY, offsetY, theta );

                if ( mtY < lowestY ) {
                    lowestX = mtX;
                    lowestY = mtY;
                }
            }
            else if ( pathElement instanceof LineTo ) {
                final LineTo lt = ( LineTo ) pathElement;

                currentX = lt.getX();
                currentY = lt.getY();

                final double ltX = transformX( currentX, currentY, offsetX, theta );
                final double ltY = transformY( currentX, currentY, offsetY, theta );

                if ( ltY < lowestY ) {
                    lowestX = ltX;
                    lowestY = ltY;
                }
            }
            else if ( pathElement instanceof ArcTo ) {
                final ArcTo at = ( ArcTo ) pathElement;

                final double startX = currentX;
                final double startY = currentY;

                final double startXTransformed = transformX( startX, startY, offsetX, theta );
                final double startYTransformed = transformY( startX, startY, offsetY, theta );

                if ( startYTransformed < lowestY ) {
                    lowestX = startXTransformed;
                    lowestY = startYTransformed;
                }

                if ( at.isAbsolute() ) {
                    currentX = at.getX();
                    currentY = at.getY();
                }
                else {
                    currentX += at.getX();
                    currentY += at.getY();
                }

                final double endX = currentX;
                final double endY = currentY;

                final double endXTransformed = transformX( endX, endY, offsetX, theta );
                final double endYTransformed = transformY( endX, endY, offsetY, theta );

                if ( endYTransformed < lowestY ) {
                    lowestX = endXTransformed;
                    lowestY = endYTransformed;
                }

                // NOTE: The arc mid-point is either being calculated
                // incorrectly, or transformed incorrectly, but if we pass in
                // the transformed end points it is also wrong.
                // final Point2D arcMid = getArcMidPoint( at, startX, startY,
                // endX, endY );
                // final double arcMidX = arcMid.getX();
                // final double arcMidY = arcMid.getY();
                //
                // final double arcMidXTransformed = transformX( arcMidX,
                // arcMidY, offsetX, theta );
                // final double arcMidYTransformed = transformY( arcMidX,
                // arcMidY, offsetY, theta );
                //
                // if ( arcMidYTransformed < lowestY ) {
                // lowestX = arcMidXTransformed;
                // lowestY = arcMidYTransformed;
                // }
            }
            else if ( pathElement instanceof QuadCurveTo ) {
                final QuadCurveTo qct = ( QuadCurveTo ) pathElement;

                final double startX = transformX( currentX, currentY, offsetX, theta );
                final double startY = transformY( currentX, currentY, offsetY, theta );

                if ( startY < lowestY ) {
                    lowestX = startX;
                    lowestY = startY;
                }

                currentX = qct.getX();
                currentY = qct.getY();

                final double endX = transformX( currentX, currentY, offsetX, theta );
                final double endY = transformY( currentX, currentY, offsetY, theta );

                if ( endY < lowestY ) {
                    lowestX = endX;
                    lowestY = endY;
                }

                final double controlX = transformX( qct.getControlX(),
                                                    qct.getControlY(),
                                                    offsetX,
                                                    theta );
                final double controlY = transformY( qct.getControlX(),
                                                    qct.getControlY(),
                                                    offsetY,
                                                    theta );

                final double midX = getQauadraticBezierValue( 0.5d, startX, controlX, endX );
                final double midY = getQauadraticBezierValue( 0.5d, startY, controlY, endY );

                if ( midY < lowestY ) {
                    lowestX = midX;
                    lowestY = midY;
                }
            }
            else if ( pathElement instanceof CubicCurveTo ) {
                final CubicCurveTo cct = ( CubicCurveTo ) pathElement;

                final double startX = transformX( currentX, currentY, offsetX, theta );
                final double startY = transformY( currentX, currentY, offsetY, theta );

                if ( startY < lowestY ) {
                    lowestX = startX;
                    lowestY = startY;
                }

                currentX = cct.getX();
                currentY = cct.getY();

                final double endX = transformX( currentX, currentY, offsetX, theta );
                final double endY = transformY( currentX, currentY, offsetY, theta );

                if ( endY < lowestY ) {
                    lowestX = endX;
                    lowestY = endY;
                }

                final double controlX1 = transformX( cct.getControlX1(),
                                                     cct.getControlY1(),
                                                     offsetX,
                                                     theta );
                final double controlY1 = transformY( cct.getControlX1(),
                                                     cct.getControlY1(),
                                                     offsetY,
                                                     theta );

                final double controlX2 = transformX( cct.getControlX2(),
                                                     cct.getControlY2(),
                                                     offsetX,
                                                     theta );
                final double controlY2 = transformY( cct.getControlX2(),
                                                     cct.getControlY2(),
                                                     offsetY,
                                                     theta );

                final double midX = getCubicBezierValue( 0.5d, startX, controlX1, controlX2, endX );
                final double midY = getCubicBezierValue( 0.5d, startY, controlY1, controlY2, endY );

                if ( midY < lowestY ) {
                    lowestX = midX;
                    lowestY = midY;
                }
            }
            else if ( pathElement instanceof HLineTo ) {
                final HLineTo hlt = ( HLineTo ) pathElement;

                currentX = hlt.getX();

                final double endX = transformX( currentX, currentY, offsetX, theta );
                final double endY = transformY( currentX, currentY, offsetY, theta );

                if ( endY < lowestY ) {
                    lowestX = endX;
                    lowestY = endY;
                }
            }
            else if ( pathElement instanceof VLineTo ) {
                final VLineTo vlt = ( VLineTo ) pathElement;

                currentY = vlt.getY();

                final double endX = transformX( currentX, currentY, offsetX, theta );
                final double endY = transformY( currentX, currentY, offsetY, theta );

                if ( endY < lowestY ) {
                    lowestX = endX;
                    lowestY = endY;
                }
            }
        }

        // Return the lowest coordinates as a Point2D instance.
        final Pair< Double, Double > lowestPoint = new Pair<>( lowestX, lowestY );

        return lowestPoint;
    }

    /**
     * This method returns a Point2D that represents the (x, y) coordinate
     * pair of the mid-point of a supplied arc, using the ArcTo representation.
     * <p>
     * NOTE: This method does not yet compute the correct point, so needs to
     * be debugged after switching the JM-1P enclosure back to using an
     * elliptical arc instead of a quadratic curve (and verifying arc height).
     *
     * @param arcTo
     *            An ArcTo instance, used to represent the reference arc
     * @param startX
     *            The x-coordinate of the start point of the arc
     * @param startY
     *            The y-coordinate of the start point of the arc
     * @param endX
     *            The x-coordinate of the end point of the arc
     * @param endY
     *            The y-coordinate of the end point of the arc
     * @return A Point2D representing the (x, y) coordinate pair of the
     *         mid-point of the arc
     */
    public static Point2D getArcMidPoint( final ArcTo arcTo,
                                          final double startX,
                                          final double startY,
                                          final double endX,
                                          final double endY ) {
        // Compute the half distance between the current and the final point,
        // which effectively is the intersection point of the bisectors of the
        // two dimensions of the tightest bounding box for the elliptical arc.
        final double dx2 = 0.5d * ( startX - endX );
        final double dy2 = 0.5d * ( startY - endY );

        // Convert ellipse rotation angle from degrees to radians.
        final double xAxisRotationRadians = Math.toRadians( arcTo.getXAxisRotation() );
        final double cosAngle = Math.cos( xAxisRotationRadians );
        final double sinAngle = Math.sin( xAxisRotationRadians );

        // Step 1: Compute (x1, y1).
        final double x1 = ( cosAngle * dx2 ) + ( sinAngle * dy2 );
        final double y1 = ( -sinAngle * dx2 ) + ( cosAngle * dy2 );

        // Gather the squares of relevant terms, as pre-factors.
        double rx = Math.abs( arcTo.getRadiusX() );
        double ry = Math.abs( arcTo.getRadiusY() );
        double Prx = MathExt.sqr( rx );
        double Pry = MathExt.sqr( ry );
        final double Px1 = MathExt.sqr( x1 );
        final double Py1 = MathExt.sqr( y1 );

        // Check that the two ellipse radii are large enough, via ratios.
        final double radiiCheck = ( Px1 / Prx ) + ( Py1 / Pry );
        if ( radiiCheck > 1d ) {
            final double radialRoot = Math.sqrt( radiiCheck );
            rx *= radialRoot;
            ry *= radialRoot;

            if ( Double.isNaN( rx ) || Double.isNaN( ry ) ) {
                return new Point2D( startX, startY );
            }

            // Recalculate the squares of the adjusted radii.
            Prx = MathExt.sqr( rx );
            Pry = MathExt.sqr( ry );
        }

        // Step 2: Compute (cx1, cy1).
        final boolean localSweepFlag = arcTo.isSweepFlag();
        final boolean localLargeArcFlag = arcTo.isLargeArcFlag();
        double sign = ( ( localLargeArcFlag == localSweepFlag ) ? -1d : 1d );
        double sq = ( ( Prx * Pry ) - ( Prx * Py1 ) - ( Pry * Px1 ) )
                / ( ( Prx * Py1 ) + ( Pry * Px1 ) );
        sq = Math.max( 0d, sq );
        final double coef = ( sign * Math.sqrt( sq ) );
        final double cx1 = coef * ( ( rx * y1 ) / ry );
        final double cy1 = coef * ( -( ry * x1 ) / rx );

        // Step 3: Compute the angleStart and the angleExtent.
        final double ux = ( x1 - cx1 ) / rx;
        final double uy = ( y1 - cy1 ) / ry;
        final double vx = ( -x1 - cx1 ) / rx;
        final double vy = ( -y1 - cy1 ) / ry;

        // Compute the angle start.
        double n = Math.hypot( ux, uy );
        double p = ux; // (1d * ux) + (0d * uy)
        sign = ( ( uy < 0d ) ? -1d : 1d );
        double angleStartDegrees = Math.toDegrees( sign * Math.acos( p / n ) );

        // Compute the angle extent.
        n = Math.sqrt( ( MathExt.sqr( ux ) + MathExt.sqr( uy ) )
                * ( MathExt.sqr( vx ) + MathExt.sqr( vy ) ) );
        p = ( ux * vx ) + ( uy * vy );
        sign = ( ( ( ux * vy ) - ( uy * vx ) ) < 0d ) ? -1d : 1d;
        double angleExtentDegrees = Math.toDegrees( sign * Math.acos( p / n ) );
        if ( !localSweepFlag && ( angleExtentDegrees > 0d ) ) {
            angleExtentDegrees -= 360d;
        }
        else if ( localSweepFlag && ( angleExtentDegrees < 0d ) ) {
            angleExtentDegrees += 360d;
        }
        angleExtentDegrees = angleExtentDegrees % 360d;
        angleStartDegrees = angleStartDegrees % 360d;

        // Now all we need is the mid-way angle, and simple trigonometry then
        // gives us the (x, y) coordinate pair for the arc mid-point.
        final double arcMidAngleDegrees = 0.5d * ( angleStartDegrees + angleExtentDegrees );
        final double arcMidAngleRadians = Math.toRadians( arcMidAngleDegrees );
        final double arcMidX = Math.cos( arcMidAngleRadians );
        final double arcMidY = Math.sin( arcMidAngleRadians );
        final Point2D arcMidPoint = new Point2D( arcMidX, arcMidY );

        return arcMidPoint;
    }

    /*
     * Get a BoundingBox converted from Meters to current Distance Unit.
     */
    public static BoundingBox getBoundingBoxInDistanceUnit( final Bounds bounds,
                                                            final DistanceUnit distanceUnit ) {
        final BoundingBox boundingBox = getBoundingBoxInDistanceUnit( bounds,
                                                                      DistanceUnit.METERS,
                                                                      distanceUnit );

        return boundingBox;
    }

    /*
     * Get a BoundingBox converted from current to specified Distance Unit.
     */
    public static BoundingBox getBoundingBoxInDistanceUnit( final Bounds bounds,
                                                            final DistanceUnit oldDistanceUnit,
                                                            final DistanceUnit newDistanceUnit ) {
        final double x = UnitConversion
                .convertDistance( bounds.getMinX(), oldDistanceUnit, newDistanceUnit );
        final double y = UnitConversion
                .convertDistance( bounds.getMinY(), oldDistanceUnit, newDistanceUnit );
        final double width = UnitConversion
                .convertDistance( bounds.getWidth(), oldDistanceUnit, newDistanceUnit );
        final double height = UnitConversion
                .convertDistance( bounds.getHeight(), oldDistanceUnit, newDistanceUnit );
        final BoundingBox boundingBox = new BoundingBox( x, y, width, height );

        return boundingBox;
    }

    /*
     * Get a BoundingBox converted from Meters to current Distance Unit.
     */
    public static BoundingBox getBoundingBoxInDistanceUnit( final Extents2D extents,
                                                            final DistanceUnit distanceUnit ) {
        final BoundingBox boundingBox = getBoundingBoxInDistanceUnit( extents,
                                                                      DistanceUnit.METERS,
                                                                      distanceUnit );

        return boundingBox;
    }

    /*
     * Get a BoundingBox converted from current to specified Distance Unit.
     */
    public static BoundingBox getBoundingBoxInDistanceUnit( final Extents2D extents,
                                                            final DistanceUnit oldDistanceUnit,
                                                            final DistanceUnit newDistanceUnit ) {
        final double x = UnitConversion
                .convertDistance( extents.getX(), oldDistanceUnit, newDistanceUnit );
        final double y = UnitConversion
                .convertDistance( extents.getY(), oldDistanceUnit, newDistanceUnit );
        final double width = UnitConversion
                .convertDistance( extents.getWidth(), oldDistanceUnit, newDistanceUnit );
        final double height = UnitConversion
                .convertDistance( extents.getHeight(), oldDistanceUnit, newDistanceUnit );
        final BoundingBox boundingBox = new BoundingBox( x, y, width, height );

        return boundingBox;
    }

    /*
     * Get a BoundingBox converted from current Distance Unit to Meters.
     */
    public static BoundingBox getBoundingBoxInMeters( final Bounds bounds,
                                                      final DistanceUnit distanceUnit ) {
        final BoundingBox boundingBox = getBoundingBoxInDistanceUnit( bounds,
                                                                      distanceUnit,
                                                                      DistanceUnit.METERS );

        return boundingBox;
    }

    /*
     * Get a BoundingBox converted from current Distance Unit to Meters.
     */
    public static BoundingBox getBoundingBoxInMeters( final Extents2D extents,
                                                      final DistanceUnit distanceUnit ) {
        final BoundingBox boundingBox = getBoundingBoxInDistanceUnit( extents,
                                                                      distanceUnit,
                                                                      DistanceUnit.METERS );

        return boundingBox;
    }

    public static double getCenterX( final Bounds bbox ) {
        return bbox.getMinX() + ( 0.5d * bbox.getWidth() );
    }

    public static double getCenterX( final Rectangle2D bbox ) {
        return bbox.getMinX() + ( 0.5d * bbox.getWidth() );
    }

    public static double getCenterY( final Bounds bbox ) {
        return bbox.getMinY() + ( 0.5d * bbox.getHeight() );
    }

    public static double getCenterY( final Rectangle2D bbox ) {
        return bbox.getMinY() + ( 0.5d * bbox.getHeight() );
    }

    /**
     * This method returns the value along a Cubic Bezier Curve at a given
     * position. It must be called separately for each coordinate in a Cartesian
     * coordinate pair (i.e. once for the x-axis, and once for the y-axis).
     *
     * @param position
     *            The position along the curve, from t=0 to t=1 (inclusive)
     * @param start
     *            The start point coordinate for the Cubic Bezier Curve
     * @param control1
     *            The first control point coordinate for the Cubic Bezier Curve
     * @param control2
     *            The second control point coordinate for the Cubic Bezier Curve
     * @param end
     *            The end point coordinate for the Cubic Bezier Curve
     * @return
     *         The coordinate for the point on the Cubic Bezier Curve at the
     *         given position from t=0 to t=1 (inclusive)
     */
    public static double getCubicBezierValue( final double position,
                                              final double start,
                                              final double control1,
                                              final double control2,
                                              final double end ) {
        final double inversePosition = 1d - position;

        final double b1 = Math.pow( position, 3d );
        final double b2 = 3d * MathExt.sqr( position ) * inversePosition;
        final double b3 = 3d * MathExt.sqr( inversePosition ) * position;
        final double b4 = Math.pow( inversePosition, 3d );

        final double cubicBezierValue = ( b1 * start ) + ( b2 * control1 ) + ( b3 * control2 )
                + ( b4 * end );

        return cubicBezierValue;
    }

    /*
     * Get an Extents2D converted from Meters to specified Distance Unit.
     */
    public static Extents2D getExtentsInDistanceUnit( final Extents2D extents,
                                                      final DistanceUnit distanceUnit ) {
        final Extents2D extents2D = getExtentsInDistanceUnit( extents,
                                                              DistanceUnit.METERS,
                                                              distanceUnit );

        return extents2D;
    }

    /*
     * Get an Extents2D converted from current to specified Distance Unit.
     */
    public static Extents2D getExtentsInDistanceUnit( final Extents2D extents,
                                                      final DistanceUnit oldDistanceUnit,
                                                      final DistanceUnit newDistanceUnit ) {
        final double x = UnitConversion
                .convertDistance( extents.getX(), oldDistanceUnit, newDistanceUnit );
        final double y = UnitConversion
                .convertDistance( extents.getY(), oldDistanceUnit, newDistanceUnit );
        final double width = UnitConversion
                .convertDistance( extents.getWidth(), oldDistanceUnit, newDistanceUnit );
        final double height = UnitConversion
                .convertDistance( extents.getHeight(), oldDistanceUnit, newDistanceUnit );
        final Extents2D extents2D = new Extents2D( x, y, width, height );

        return extents2D;
    }

    /*
     * Get an Extents2D converted from current Distance Unit to Meters.
     */
    public static Extents2D getExtentsInMeters( final Extents2D extents,
                                                final DistanceUnit distanceUnit ) {
        final Extents2D extents2D = getExtentsInDistanceUnit( extents,
                                                              distanceUnit,
                                                              DistanceUnit.METERS );

        return extents2D;
    }

    /**
     * Creates a BoundingBox covering the intersection of the two given Bounds.
     *
     * @param rect1
     *            The first of two rectangles to intersect
     * @param rect2
     *            The second of two rectangles to intersect
     * @return A combined bounding box, if the two intersect
     */
    public static BoundingBox getIntersection( final Bounds rect1, final Bounds rect2 ) {
        final double x1 = Math.max( rect1.getMinX(), rect2.getMinX() );
        final double y1 = Math.max( rect1.getMinY(), rect2.getMinY() );
        final double z1 = Math.max( rect1.getMinZ(), rect2.getMinZ() );
        final double x2 = Math.min( rect1.getMaxX(), rect2.getMaxX() );
        final double y2 = Math.min( rect1.getMaxY(), rect2.getMaxY() );
        final double z2 = Math.min( rect1.getMaxZ(), rect2.getMaxZ() );
        final double width = x2 - x1;
        final double height = y2 - y1;
        final double depth = z2 - z1;
        final boolean intersects = ( width >= 0 ) && ( height >= 0 ) && ( depth >= 0 );
        if ( !intersects ) {
            return null;
        }
        final BoundingBox intersection = new BoundingBox( x1, y1, z1, width, height, depth );

        return intersection;
    }

    /**
     * Creates a Rectangle2D covering the intersection of the two given
     * Rectangle2D's.
     *
     * @param rect1
     *            The first of two rectangles to intersect
     * @param rect2
     *            The second of two rectangles to intersect
     * @return A combined rectangle, if the two intersect
     */
    public static Rectangle2D getIntersection( final Rectangle2D rect1, final Rectangle2D rect2 ) {
        final double x1 = Math.max( rect1.getMinX(), rect2.getMinX() );
        final double y1 = Math.max( rect1.getMinY(), rect2.getMinY() );
        final double x2 = Math.min( rect1.getMaxX(), rect2.getMaxX() );
        final double y2 = Math.min( rect1.getMaxY(), rect2.getMaxY() );
        final double width = x2 - x1;
        final double height = y2 - y1;
        final boolean intersects = ( width >= 0 ) && ( height >= 0 );
        if ( !intersects ) {
            return null;
        }
        final Rectangle2D intersection = new Rectangle2D( x1, y1, width, height );

        return intersection;
    }

    /* Get an AWT line, converted from JavaFX. */
    public static java.awt.geom.Line2D getLine( final Line fxLine ) {
        final java.awt.geom.Line2D awtLine = new java.awt.geom.Line2D.Double( fxLine.getStartX(),
                                                                              fxLine.getStartY(),
                                                                              fxLine.getEndX(),
                                                                              fxLine.getEndY() );
        return awtLine;
    }

    /**
     * Returns the octant of a 3D point relative to an origin:
     * 1, 2, 3, 4, 5, 6, 7, or 8
     *
     * @param point
     *            The point to judge relative to the origin
     * @param origin
     *            The origin to reference for determining the octant of the
     *            supplied 3D point
     * @return The octant number for a supplied 3D point
     */
    public static int getOctant( final Point3D point, final Point3D origin ) {
        if ( point.getZ() < origin.getZ() ) {
            if ( point.getX() < origin.getX() ) {
                if ( point.getY() >= origin.getY() ) {
                    return 6;
                }
                return 7;
            }
            if ( point.getY() >= origin.getY() ) {
                return 5;
            }
            return 8;
        }
        if ( point.getX() < origin.getX() ) {
            if ( point.getY() >= origin.getY() ) {
                return 2;
            }
            return 3;
        }
        if ( point.getY() >= origin.getY() ) {
            return 1;
        }
        return 4;
    }

    /* Get an AWT point, converted from JavaFX. */
    public static java.awt.geom.Point2D getPoint( final Point2D fxPoint ) {
        final java.awt.geom.Point2D awtPoint = new java.awt.geom.Point2D.Double( fxPoint.getX(),
                                                                                 fxPoint.getY() );
        return awtPoint;
    }

    /*
     * Get a Point2D converted from Meters to current Distance Unit.
     */
    public static Point2D getPointInDistanceUnit( final double xMeters,
                                                  final double yMeters,
                                                  final DistanceUnit distanceUnit ) {
        final double x =
                       UnitConversion.convertDistance( xMeters, DistanceUnit.METERS, distanceUnit );
        final double y =
                       UnitConversion.convertDistance( yMeters, DistanceUnit.METERS, distanceUnit );
        final Point2D point = new Point2D( x, y );

        return point;
    }

    /*
     * Get a Point2D converted from Meters to current Distance Unit.
     */
    public static Point2D getPointInDistanceUnit( final Point2D pointMeters,
                                                  final DistanceUnit distanceUnit ) {
        final double x = UnitConversion
                .convertDistance( pointMeters.getX(), DistanceUnit.METERS, distanceUnit );
        final double y = UnitConversion
                .convertDistance( pointMeters.getY(), DistanceUnit.METERS, distanceUnit );
        final Point2D point = new Point2D( x, y );

        return point;
    }

    /*
     * Get a Point2D converted from current Distance Unit to Meters.
     */
    public static Point2D getPointInMeters( final double x,
                                            final double y,
                                            final DistanceUnit distanceUnit ) {
        final double xMeters =
                             UnitConversion.convertDistance( x, distanceUnit, DistanceUnit.METERS );
        final double yMeters =
                             UnitConversion.convertDistance( y, distanceUnit, DistanceUnit.METERS );
        final Point2D pointMeters = new Point2D( xMeters, yMeters );

        return pointMeters;
    }

    /*
     * Get a Point2D converted from current Distance Unit to Meters.
     */
    public static Point2D getPointInMeters( final Point2D point, final DistanceUnit distanceUnit ) {
        final double xMeters = UnitConversion
                .convertDistance( point.getX(), distanceUnit, DistanceUnit.METERS );
        final double yMeters = UnitConversion
                .convertDistance( point.getY(), distanceUnit, DistanceUnit.METERS );
        final Point2D pointMeters = new Point2D( xMeters, yMeters );

        return pointMeters;
    }

    /**
     * This method returns the value along a Quadratic Bezier Curve at a given
     * position. It must be called separately for each coordinate in a Cartesian
     * coordinate pair (i.e. once for the x-axis, and once for the y-axis).
     *
     * @param position
     *            The position along the curve, from t=0 to t=1 (inclusive)
     * @param start
     *            The start point coordinate for the Quadratic Bezier Curve
     * @param control
     *            The control point coordinate for the Quadratic Bezier Curve
     * @param end
     *            The end point coordinate for the Quadratic Bezier Curve
     * @return
     *         The coordinate for the point on the Quadratic Bezier Curve at the
     *         given position from t=0 to t=1 (inclusive)
     */
    public static double getQauadraticBezierValue( final double position,
                                                   final double start,
                                                   final double control,
                                                   final double end ) {
        final double inversePosition = 1d - position;

        final double b1 = MathExt.sqr( inversePosition );
        final double b2 = 2d * inversePosition * position;
        final double b3 = MathExt.sqr( position );

        final double quadraticBezierValue = ( b1 * start ) + ( b2 * control ) + ( b3 * end );

        return quadraticBezierValue;
    }

    /**
     * Returns the quadrant of a 2D point relative to an origin:
     * 1, 2, 3, or 4
     *
     * @param point
     *            The point to judge relative to the origin
     * @param origin
     *            The origin to reference for determining the quadrant of the
     *            supplied 2D point
     * @return The quadrant number for a supplied 2D point
     */
    public static int getQuadrant( final Point2D point, final Point2D origin ) {
        if ( point.getX() < origin.getX() ) {
            if ( point.getY() >= origin.getY() ) {
                return 2;
            }
            return 3;
        }
        if ( point.getY() >= origin.getY() ) {
            return 1;
        }
        return 4;
    }

    /*
     * Get a Rectangle shape, converted from Meters to specified Distance Unit.
     */
    public static Rectangle getRectangleInDistanceUnit( final Bounds boundsMeters,
                                                        final DistanceUnit distanceUnit ) {
        final double x = UnitConversion
                .convertDistance( boundsMeters.getMinX(), DistanceUnit.METERS, distanceUnit );
        final double y = UnitConversion
                .convertDistance( boundsMeters.getMinY(), DistanceUnit.METERS, distanceUnit );
        final double width = UnitConversion
                .convertDistance( boundsMeters.getWidth(), DistanceUnit.METERS, distanceUnit );
        final double height = UnitConversion
                .convertDistance( boundsMeters.getHeight(), DistanceUnit.METERS, distanceUnit );
        final Rectangle rectangle = new Rectangle( x, y, width, height );

        return rectangle;
    }

    /*
     * Get a Rectangle2D converted from current to specified Distance Unit.
     */
    public static Rectangle2D getRectangleInDistanceUnit( final Bounds bounds,
                                                          final DistanceUnit oldDistanceUnit,
                                                          final DistanceUnit newDistanceUnit ) {
        final double x = UnitConversion
                .convertDistance( bounds.getMinX(), oldDistanceUnit, newDistanceUnit );
        final double y = UnitConversion
                .convertDistance( bounds.getMinY(), oldDistanceUnit, newDistanceUnit );
        final double width = UnitConversion
                .convertDistance( bounds.getWidth(), oldDistanceUnit, newDistanceUnit );
        final double height = UnitConversion
                .convertDistance( bounds.getHeight(), oldDistanceUnit, newDistanceUnit );
        final Rectangle2D rectangle2D = new Rectangle2D( x, y, width, height );

        return rectangle2D;
    }

    /*
     * Get a Rectangle2D converted from current to specified Distance Unit.
     */
    public static Rectangle2D getRectangleInDistanceUnit( final Extents2D extents,
                                                          final DistanceUnit oldDistanceUnit,
                                                          final DistanceUnit newDistanceUnit ) {
        final double x = UnitConversion
                .convertDistance( extents.getX(), oldDistanceUnit, newDistanceUnit );
        final double y = UnitConversion
                .convertDistance( extents.getY(), oldDistanceUnit, newDistanceUnit );
        final double width = UnitConversion
                .convertDistance( extents.getWidth(), oldDistanceUnit, newDistanceUnit );
        final double height = UnitConversion
                .convertDistance( extents.getHeight(), oldDistanceUnit, newDistanceUnit );
        final Rectangle2D rectangle2D = new Rectangle2D( x, y, width, height );

        return rectangle2D;
    }

    /*
     * Get a Rectangle2D converted from current to specified Distance Unit.
     */
    public static Rectangle2D getRectangleInDistanceUnit( final Rectangle rectangle,
                                                          final DistanceUnit oldDistanceUnit,
                                                          final DistanceUnit newDistanceUnit ) {
        final double x = UnitConversion
                .convertDistance( rectangle.getX(), oldDistanceUnit, newDistanceUnit );
        final double y = UnitConversion
                .convertDistance( rectangle.getY(), oldDistanceUnit, newDistanceUnit );
        final double width = UnitConversion
                .convertDistance( rectangle.getWidth(), oldDistanceUnit, newDistanceUnit );
        final double height = UnitConversion
                .convertDistance( rectangle.getHeight(), oldDistanceUnit, newDistanceUnit );
        final Rectangle2D rectangle2D = new Rectangle2D( x, y, width, height );

        return rectangle2D;
    }

    /*
     * Get a Rectangle shape, converted from Meters to specified Distance Unit.
     */
    public static Rectangle getRectangleInDistanceUnit( final Rectangle2D rectangleMeters,
                                                        final DistanceUnit distanceUnit ) {
        final double x = UnitConversion
                .convertDistance( rectangleMeters.getMinX(), DistanceUnit.METERS, distanceUnit );
        final double y = UnitConversion
                .convertDistance( rectangleMeters.getMinY(), DistanceUnit.METERS, distanceUnit );
        final double width = UnitConversion
                .convertDistance( rectangleMeters.getWidth(), DistanceUnit.METERS, distanceUnit );
        final double height = UnitConversion
                .convertDistance( rectangleMeters.getHeight(), DistanceUnit.METERS, distanceUnit );
        final Rectangle rectangle = new Rectangle( x, y, width, height );

        return rectangle;
    }

    /*
     * Get a Rectangle2D converted from current to specified Distance Unit.
     */
    public static Rectangle2D getRectangleInDistanceUnit( final Rectangle2D rectangle,
                                                          final DistanceUnit oldDistanceUnit,
                                                          final DistanceUnit newDistanceUnit ) {
        final double x = UnitConversion
                .convertDistance( rectangle.getMinX(), oldDistanceUnit, newDistanceUnit );
        final double y = UnitConversion
                .convertDistance( rectangle.getMinY(), oldDistanceUnit, newDistanceUnit );
        final double width = UnitConversion
                .convertDistance( rectangle.getWidth(), oldDistanceUnit, newDistanceUnit );
        final double height = UnitConversion
                .convertDistance( rectangle.getHeight(), oldDistanceUnit, newDistanceUnit );
        final Rectangle2D rectangle2D = new Rectangle2D( x, y, width, height );

        return rectangle2D;
    }

    /*
     * Get a Rectangle2D converted from current Distance Unit to Meters.
     */
    public static Rectangle2D getRectangleInMeters( final Bounds bounds,
                                                    final DistanceUnit distanceUnit ) {
        final Rectangle2D rectangleMeters = getRectangleInDistanceUnit( bounds,
                                                                        distanceUnit,
                                                                        DistanceUnit.METERS );

        return rectangleMeters;
    }

    /*
     * Get a Rectangle2D converted from current Distance Unit to Meters.
     */
    public static Rectangle2D getRectangleInMeters( final Extents2D extents,
                                                    final DistanceUnit distanceUnit ) {
        final Rectangle2D rectangleMeters = getRectangleInDistanceUnit( extents,
                                                                        distanceUnit,
                                                                        DistanceUnit.METERS );

        return rectangleMeters;
    }

    /*
     * Get a Rectangle2D converted from current Distance Unit to Meters.
     */
    public static Rectangle2D getRectangleInMeters( final Rectangle rectangle,
                                                    final DistanceUnit distanceUnit ) {
        final Rectangle2D rectangleMeters = getRectangleInDistanceUnit( rectangle,
                                                                        distanceUnit,
                                                                        DistanceUnit.METERS );

        return rectangleMeters;
    }

    /*
     * Get a Rectangle2D converted from current Distance Unit to Meters.
     */
    public static Rectangle2D getRectangleInMeters( final Rectangle2D rectangle,
                                                    final DistanceUnit distanceUnit ) {
        final Rectangle2D rectangleMeters = getRectangleInDistanceUnit( rectangle,
                                                                        distanceUnit,
                                                                        DistanceUnit.METERS );

        return rectangleMeters;
    }

    public static java.awt.geom.Rectangle2D getRectangleMetersAwt( final Extents2D extents,
                                                                   final DistanceUnit distanceUnit ) {
        final double x = UnitConversion
                .convertDistance( extents.getX(), distanceUnit, DistanceUnit.METERS );
        final double y = UnitConversion
                .convertDistance( extents.getY(), distanceUnit, DistanceUnit.METERS );
        final double width = UnitConversion
                .convertDistance( extents.getWidth(), distanceUnit, DistanceUnit.METERS );
        final double height = UnitConversion
                .convertDistance( extents.getHeight(), distanceUnit, DistanceUnit.METERS );
        final java.awt.geom.Rectangle2D awtRectangle =
                                                     new java.awt.geom.Rectangle2D.Double( x,
                                                                                           y,
                                                                                           width,
                                                                                           height );
        return awtRectangle;
    }

    // Get an AWT rectangle in meters, converted from JavaFX.
    public static java.awt.geom.Rectangle2D getRectangleMetersAwt( final Rectangle fxRectangle,
                                                                   final DistanceUnit distanceUnit ) {
        final double x = UnitConversion
                .convertDistance( fxRectangle.getX(), distanceUnit, DistanceUnit.METERS );
        final double y = UnitConversion
                .convertDistance( fxRectangle.getY(), distanceUnit, DistanceUnit.METERS );
        final double width = UnitConversion
                .convertDistance( fxRectangle.getWidth(), distanceUnit, DistanceUnit.METERS );
        final double height = UnitConversion
                .convertDistance( fxRectangle.getHeight(), distanceUnit, DistanceUnit.METERS );
        final java.awt.geom.Rectangle2D awtRectangle =
                                                     new java.awt.geom.Rectangle2D.Double( x,
                                                                                           y,
                                                                                           width,
                                                                                           height );
        return awtRectangle;
    }

    /*
     * Returns a new transform from a supplied reference point.
     */
    public static Affine getReferencePointTransform( final Pair< Double, Double > referencePoint ) {
        final Affine affineTransform = new Affine();

        // Effectively move to the reference point as the origin.
        affineTransform.appendTranslation( referencePoint.getKey(), referencePoint.getValue() );

        return affineTransform;
    }

    /*
     * Returns a new transform from a supplied reference point.
     */
    public static Affine getReferencePointTransform( final Point2D referencePoint ) {
        final Affine affineTransform = new Affine();

        // Effectively move to the reference point as the origin.
        affineTransform.appendTranslation( referencePoint.getX(), referencePoint.getY() );

        return affineTransform;
    }

    /**
     * Ignores Z/depth.
     *
     * @param area
     *            treated like a 2D rectangle
     * @param line
     *            the line to intersect with the area
     * @see #intersects(Rectangle2D, Line)
     * @return true if intersects, false otherwise
     */
    public static boolean intersects( final Bounds area, final Line line ) {
        final Rectangle2D rect = new Rectangle2D( area.getMinX(),
                                                  area.getMinY(),
                                                  area.getWidth(),
                                                  area.getHeight() );
        return intersects( rect, line );
    }

    /*
     * Tests intersection and containment.
     * @see #intersectsLine(Rectangle2D, Line)
     * @see #contains(Rectangle2D, Line)
     */
    public static boolean intersects( final Rectangle2D area, final Line line ) {
        return intersectsLine( area, line ) || contains( area, line );
    }

    /**
     * Tests if the specified line segment intersects the interior of this
     * <code>Rectangle2D</code>.
     *
     * @param rect
     *            the rectangle to intersect with the implied line
     * @param initialX1
     *            the X coordinate of the start point of the specified line
     *            segment
     * @param initialY1
     *            the Y coordinate of the start point of the specified line
     *            segment
     * @param initialX2
     *            the X coordinate of the end point of the specified line
     *            segment
     * @param initialY2
     *            the Y coordinate of the end point of the specified line
     *            segment
     * @return <code>true</code> if the specified line segment intersects the
     *         interior of this <code>Rectangle2D</code>; <code>false</code>
     *         otherwise.
     * @since 1.2
     */
    public static boolean intersectsLine( final Rectangle2D rect,
                                          final double initialX1,
                                          final double initialY1,
                                          final double initialX2,
                                          final double initialY2 ) {
        double x1 = initialX1;
        double y1 = initialY1;
        final double x2 = initialX2;
        final double y2 = initialY2;

        int out1, out2;

        if ( ( out2 = outcode( rect, x2, y2 ) ) == 0 ) {
            return true;
        }

        while ( ( out1 = outcode( rect, x1, y1 ) ) != 0 ) {
            if ( ( out1 & out2 ) != 0 ) {
                return false;
            }
            if ( ( out1 & ( OUT_LEFT | OUT_RIGHT ) ) != 0 ) {
                double x = rect.getMinX();
                if ( ( out1 & OUT_RIGHT ) != 0 ) {
                    x += rect.getWidth();
                }
                y1 = y1 + ( ( ( x - x1 ) * ( y2 - y1 ) ) / ( x2 - x1 ) );
                x1 = x;
            }
            else {
                double y = rect.getMinY();
                if ( ( out1 & OUT_BOTTOM ) != 0 ) {
                    y += rect.getHeight();
                }
                x1 = x1 + ( ( ( y - y1 ) * ( x2 - x1 ) ) / ( y2 - y1 ) );
                y1 = y;
            }
        }

        return true;
    }

    /**
     * Tests if the specified line segment intersects the interior of this
     * <code>Rectangle2D</code>.
     *
     * @param rect
     *            the rectangle to intersect with the implied line
     * @param line
     *            the specified {@link Line} to test for intersection with the
     *            interior of this <code>Rectangle2D</code>
     * @return <code>true</code> if the specified <code>Line2D</code> intersects
     *         the interior of this <code>Rectangle2D</code>; <code>false</code>
     *         otherwise.
     * @since 1.2
     * @see #intersectsLine(Rectangle2D, double, double, double, double)
     */
    public static boolean intersectsLine( final Rectangle2D rect, final Line line ) {
        return intersectsLine( rect,
                               line.getStartX(),
                               line.getStartY(),
                               line.getEndX(),
                               line.getEndY() );
    }

    public static Point2D negatePoint2D( final Point2D point2D ) {
        final Point2D negatedPoint2D = new Point2D( -point2D.getX(), -point2D.getY() );
        return negatedPoint2D;
    }

    public static Point2D negatePoint2D( final Point2D point2D, final Axis axis ) {
        switch ( axis ) {
        case X:
            return new Point2D( -point2D.getX(), point2D.getY() );
        case Y:
            return new Point2D( point2D.getX(), -point2D.getY() );
        case Z:
            return new Point2D( point2D.getX(), point2D.getY() );
        default:
            return Point2D.ZERO;
        }
    }

    public static Point3D negatePoint3D( final Point3D point3D ) {
        final Point3D negatedPoint3D = new Point3D( -point3D.getX(),
                                                    -point3D.getY(),
                                                    -point3D.getZ() );
        return negatedPoint3D;
    }

    public static Point3D negatePoint3D( final Point3D point3D, final Axis axis ) {
        switch ( axis ) {
        case X:
            return new Point3D( -point3D.getX(), point3D.getY(), point3D.getZ() );
        case Y:
            return new Point3D( point3D.getX(), -point3D.getY(), point3D.getZ() );
        case Z:
            return new Point3D( point3D.getX(), point3D.getY(), -point3D.getZ() );
        default:
            return Point3D.ZERO;
        }
    }

    /*
     * @since 1.2
     */
    public static int outcode( final Rectangle2D rect, final double x, final double y ) {
        int out = 0;
        if ( rect.getWidth() <= 0 ) {
            out |= OUT_LEFT | OUT_RIGHT;
        }
        else if ( x < rect.getMinX() ) {
            out |= OUT_LEFT;
        }
        else if ( x > ( rect.getMinX() + rect.getWidth() ) ) {
            out |= OUT_RIGHT;
        }
        if ( rect.getHeight() <= 0 ) {
            out |= OUT_TOP | OUT_BOTTOM;
        }
        else if ( y < rect.getMinY() ) {
            out |= OUT_TOP;
        }
        else if ( y > ( rect.getMinY() + rect.getHeight() ) ) {
            out |= OUT_BOTTOM;
        }
        return out;
    }

    public static Point2D projectToPlane( final Point3D point3D,
                                          final OrthogonalAxes orthogonalAxes ) {
        // Project a 3D point to a plane defined by an orthogonal axis pair.
        switch ( orthogonalAxes ) {
        case XY:
            return new Point2D( point3D.getX(), point3D.getY() );
        case XZ:
            return new Point2D( point3D.getX(), point3D.getZ() );
        case YZ:
            return new Point2D( point3D.getY(), point3D.getZ() );
        default:
            return Point2D.ZERO;
        }
    }

    /**
     * Returns the distance from a point to a line. The distance measured is the
     * distance between the specified point and the closest point on the
     * infinitely-extended line defined by the specified coordinates. If the
     * specified point intersects the line, this method returns 0.0.
     *
     * @param x1
     *            the X coordinate of the start point of the specified line
     * @param y1
     *            the Y coordinate of the start point of the specified line
     * @param x2
     *            the X coordinate of the end point of the specified line
     * @param y2
     *            the Y coordinate of the end point of the specified line
     * @param px
     *            the X coordinate of the specified point being measured against
     *            the specified line
     * @param py
     *            the Y coordinate of the specified point being measured against
     *            the specified line
     * @return a double value that is the distance from the specified point to
     *         the specified line.
     * @see #ptSegDist(double, double, double, double, double, double)
     * @since 1.2
     */
    public static double ptLineDist( final double x1,
                                     final double y1,
                                     final double x2,
                                     final double y2,
                                     final double px,
                                     final double py ) {
        return Math.sqrt( ptLineDistSq( x1, y1, x2, y2, px, py ) );
    }

    /**
     * Returns the distance from a point to this line. The distance measured is
     * the distance between the specified point and the closest point on the
     * infinitely-extended line defined by this <code>Line2D</code>. If the
     * specified point intersects the line, this method returns 0.0.
     *
     * @param line
     *            the line to use to measure point distance
     * @param px
     *            the X coordinate of the specified point being measured against
     *            this line
     * @param py
     *            the Y coordinate of the specified point being measured against
     *            this line
     * @return a double value that is the distance from a specified point to the
     *         current line.
     * @see #ptSegDist(Line, double, double)
     * @since 1.2
     */
    public static double ptLineDist( final Line line, final double px, final double py ) {
        return ptLineDist( line
                .getStartX(), line.getStartY(), line.getEndX(), line.getEndY(), px, py );
    }

    /**
     * Returns the distance from a <code>Point2D</code> to this line. The
     * distance measured is the distance between the specified point and the
     * closest point on the infinitely-extended line defined by this
     * <code>Line2D</code>. If the specified point intersects the line, this
     * method returns 0.0.
     *
     * @param line
     *            the line to use to measure line distance
     * @param pt
     *            the specified <code>Point2D</code> being measured
     * @return a double value that is the distance from a specified
     *         <code>Point2D</code> to the current line.
     * @since 1.2
     */
    public static double ptLineDist( final Line line, final Point2D pt ) {
        return ptLineDist( line.getStartX(),
                           line.getStartY(),
                           line.getEndX(),
                           line.getEndY(),
                           pt.getX(),
                           pt.getY() );
    }

    /**
     * Returns the square of the distance from a point to a line. The distance
     * measured is the distance between the specified point and the closest
     * point on the infinitely-extended line defined by the specified
     * coordinates. If the specified point intersects the line, this method
     * returns 0.0.
     *
     * @param x1
     *            the X coordinate of the start point of the specified line
     * @param y1
     *            the Y coordinate of the start point of the specified line
     * @param x2
     *            the X coordinate of the end point of the specified line
     * @param y2
     *            the Y coordinate of the end point of the specified line
     * @param px
     *            the X coordinate of the specified point being measured against
     *            the specified line
     * @param py
     *            the Y coordinate of the specified point being measured against
     *            the specified line
     * @return a double value that is the square of the distance from the
     *         specified point to the specified line.
     * @see #ptSegDistSq(double, double, double, double, double, double)
     * @since 1.2
     */
    public static double ptLineDistSq( final double x1,
                                       final double y1,
                                       final double x2,
                                       final double y2,
                                       final double px,
                                       final double py ) {
        // Adjust vectors relative to x1,y1
        // x2,y2 becomes relative vector from x1,y1 to end of segment
        final double x2Adjusted = x2 - x1;
        final double y2Adjusted = y2 - y1;
        // px,py becomes relative vector from x1,y1 to test point
        final double pxAdjusted = px - x1;
        final double pyAdjusted = py - y1;
        final double dotprod = ( pxAdjusted * x2Adjusted ) + ( pyAdjusted * y2Adjusted );
        // dotprod is the length of the px,py vector
        // projected on the x1,y1=>x2,y2 vector times the
        // length of the x1,y1=>x2,y2 vector
        final double projlenSq = ( dotprod * dotprod )
                / ( ( x2Adjusted * x2Adjusted ) + ( y2Adjusted * y2Adjusted ) );
        // Distance to line is now the length of the relative point
        // vector minus the length of its projection onto the line
        double lenSq = ( ( pxAdjusted * pxAdjusted ) + ( pyAdjusted * pyAdjusted ) ) - projlenSq;
        if ( lenSq < 0 ) {
            lenSq = 0;
        }
        return lenSq;
    }

    /**
     * Returns the square of the distance from a point to this line. The
     * distance measured is the distance between the specified point and the
     * closest point on the infinitely-extended line defined by this
     * <code>Line2D</code>. If the specified point intersects the line, this
     * method returns 0.0.
     *
     * @param line
     *            the line to use to measure line distance
     * @param px
     *            the X coordinate of the specified point being measured against
     *            this line
     * @param py
     *            the Y coordinate of the specified point being measured against
     *            this line
     * @return a double value that is the square of the distance from a
     *         specified point to the current line.
     * @since 1.2
     */
    public static double ptLineDistSq( final Line line, final double px, final double py ) {
        return ptLineDistSq( line
                .getStartX(), line.getStartY(), line.getEndX(), line.getEndY(), px, py );
    }

    /**
     * Returns the square of the distance from a specified <code>Point2D</code>
     * to this line. The distance measured is the distance between the specified
     * point and the closest point on the infinitely-extended line defined by
     * this <code>Line2D</code>. If the specified point intersects the line,
     * this method returns 0.0.
     *
     * @param line
     *            the line to use to measure line distance
     * @param pt
     *            the specified <code>Point2D</code> being measured against this
     *            line
     * @return a double value that is the square of the distance from a
     *         specified <code>Point2D</code> to the current line.
     * @since 1.2
     */
    public static double ptLineDistSq( final Line line, final Point2D pt ) {
        return ptLineDistSq( line.getStartX(),
                             line.getStartY(),
                             line.getEndX(),
                             line.getEndY(),
                             pt.getX(),
                             pt.getY() );
    }

    /**
     * Returns the distance from a point to a line segment. The distance
     * measured is the distance between the specified point and the closest
     * point between the specified end points. If the specified point intersects
     * the line segment in between the end points, this method returns 0.0.
     *
     * @param x1
     *            the X coordinate of the start point of the specified line
     *            segment
     * @param y1
     *            the Y coordinate of the start point of the specified line
     *            segment
     * @param x2
     *            the X coordinate of the end point of the specified line
     *            segment
     * @param y2
     *            the Y coordinate of the end point of the specified line
     *            segment
     * @param px
     *            the X coordinate of the specified point being measured against
     *            the specified line segment
     * @param py
     *            the Y coordinate of the specified point being measured against
     *            the specified line segment
     * @return a double value that is the distance from the specified point to
     *         the specified line segment.
     * @see #ptLineDist(double, double, double, double, double, double)
     * @since 1.2
     */
    public static double ptSegDist( final double x1,
                                    final double y1,
                                    final double x2,
                                    final double y2,
                                    final double px,
                                    final double py ) {
        return Math.sqrt( ptSegDistSq( x1, y1, x2, y2, px, py ) );
    }

    /**
     * Returns the distance from a point to this line segment. The distance
     * measured is the distance between the specified point and the closest
     * point between the current line's end points. If the specified point
     * intersects the line segment in between the end points, this method
     * returns 0.0.
     *
     * @param line
     *            the line to use to measure line distance
     * @param px
     *            the X coordinate of the specified point being measured against
     *            this line segment
     * @param py
     *            the Y coordinate of the specified point being measured against
     *            this line segment
     * @return a double value that is the distance from the specified point to
     *         the current line segment.
     * @since 1.2
     */
    public static double ptSegDist( final Line line, final double px, final double py ) {
        return ptSegDist( line
                .getStartX(), line.getStartY(), line.getEndX(), line.getEndY(), px, py );
    }

    /**
     * Returns the distance from a <code>Point2D</code> to this line segment.
     * The distance measured is the distance between the specified point and the
     * closest point between the current line's end points. If the specified
     * point intersects the line segment in between the end points, this method
     * returns 0.0.
     *
     * @param line
     *            the line to use to measure line distance
     * @param pt
     *            the specified <code>Point2D</code> being measured against this
     *            line segment
     * @return a double value that is the distance from the specified
     *         <code>Point2D</code> to the current line segment.
     * @since 1.2
     */
    public static double ptSegDist( final Line line, final Point2D pt ) {
        return ptSegDist( line.getStartX(),
                          line.getStartY(),
                          line.getEndX(),
                          line.getEndY(),
                          pt.getX(),
                          pt.getY() );
    }

    /**
     * Returns the square of the distance from a point to a line segment. The
     * distance measured is the distance between the specified point and the
     * closest point between the specified end points. If the specified point
     * intersects the line segment in between the end points, this method
     * returns 0.0.
     *
     * @param x1
     *            the X coordinate of the start point of the specified line
     *            segment
     * @param y1
     *            the Y coordinate of the start point of the specified line
     *            segment
     * @param x2
     *            the X coordinate of the end point of the specified line
     *            segment
     * @param y2
     *            the Y coordinate of the end point of the specified line
     *            segment
     * @param px
     *            the X coordinate of the specified point being measured against
     *            the specified line segment
     * @param py
     *            the Y coordinate of the specified point being measured against
     *            the specified line segment
     * @return a double value that is the square of the distance from the
     *         specified point to the specified line segment.
     * @see #ptLineDistSq(double, double, double, double, double, double)
     * @since 1.2
     */
    public static double ptSegDistSq( final double x1,
                                      final double y1,
                                      final double x2,
                                      final double y2,
                                      final double px,
                                      final double py ) {
        // Adjust vectors relative to x1,y1
        // x2,y2 becomes relative vector from x1,y1 to end of segment
        final double x2Adjusted = x2 - x1;
        final double y2Adjusted = y2 - y1;

        // px,py becomes relative vector from x1,y1 to test point
        double pxAdjusted = px - x1;
        double pyAdjusted = py - y1;

        double dotprod = ( pxAdjusted * x2Adjusted ) + ( pyAdjusted * y2Adjusted );

        double projlenSq;
        if ( dotprod <= 0d ) {
            // px,py is on the side of x1,y1 away from x2,y2
            // distance to segment is length of px,py vector
            // "length of its (clipped) projection" is now 0.0
            projlenSq = 0d;
        }
        else {
            // switch to backwards vectors relative to x2,y2
            // x2,y2 are already the negative of x1,y1=>x2,y2
            // to get px,py to be the negative of px,py=>x2,y2
            // the dot product of two negated vectors is the same
            // as the dot product of the two normal vectors
            pxAdjusted = x2Adjusted - pxAdjusted;
            pyAdjusted = y2Adjusted - pyAdjusted;
            dotprod = ( pxAdjusted * x2Adjusted ) + ( pyAdjusted * y2Adjusted );
            if ( dotprod <= 0d ) {
                // px,py is on the side of x2,y2 away from x1,y1
                // distance to segment is length of (backwards) px,py vector
                // "length of its (clipped) projection" is now 0.0
                projlenSq = 0d;
            }
            else {
                // px,py is between x1,y1 and x2,y2
                // dotprod is the length of the px,py vector
                // projected on the x2,y2=>x1,y1 vector times the
                // length of the x2,y2=>x1,y1 vector
                projlenSq = MathExt.sqr( dotprod )
                        / ( MathExt.sqr( x2Adjusted ) + MathExt.sqr( y2Adjusted ) );
            }
        }

        // Distance to line is now the length of the relative point
        // vector minus the length of its projection onto the line
        // (which is zero if the projection falls outside the range
        // of the line segment).
        double lenSq = ( MathExt.sqr( pxAdjusted ) + MathExt.sqr( pyAdjusted ) ) - projlenSq;
        if ( lenSq < 0d ) {
            lenSq = 0d;
        }

        return lenSq;
    }

    /**
     * Returns the square of the distance from a point to this line segment. The
     * distance measured is the distance between the specified point and the
     * closest point between the current line's end points. If the specified
     * point intersects the line segment in between the end points, this method
     * returns 0.0.
     *
     * @param line
     *            the line to use to measure line distance
     * @param px
     *            the X coordinate of the specified point being measured against
     *            this line segment
     * @param py
     *            the Y coordinate of the specified point being measured against
     *            this line segment
     * @return a double value that is the square of the distance from the
     *         specified point to the current line segment.
     * @since 1.2
     */
    public static double ptSegDistSq( final Line line, final double px, final double py ) {
        return ptSegDistSq( line
                .getStartX(), line.getStartY(), line.getEndX(), line.getEndY(), px, py );
    }

    /**
     * Returns the square of the distance from a <code>Point2D</code> to this
     * line segment. The distance measured is the distance between the specified
     * point and the closest point between the current line's end points. If the
     * specified point intersects the line segment in between the end points,
     * this method returns 0.0.
     *
     * @param line
     *            the line to use to measure line distance
     * @param pt
     *            the specified <code>Point2D</code> being measured against this
     *            line segment.
     * @return a double value that is the square of the distance from the
     *         specified <code>Point2D</code> to the current line segment.
     * @since 1.2
     */
    public static double ptSegDistSq( final Line line, final Point2D pt ) {
        return ptSegDistSq( line.getStartX(),
                            line.getStartY(),
                            line.getEndX(),
                            line.getEndY(),
                            pt.getX(),
                            pt.getY() );
    }

    public static Rectangle2D rectangle2DFromBounds( final Bounds bounds ) {
        return new Rectangle2D( bounds.getMinX(),
                                bounds.getMinY(),
                                bounds.getWidth(),
                                bounds.getHeight() );
    }

    public static Rectangle2D rectangle2DFromExtents( final Extents2D extents ) {
        return new Rectangle2D( extents.getX(),
                                extents.getY(),
                                extents.getWidth(),
                                extents.getHeight() );
    }

    // Get an AWT rectangle, converted from generic Extents.
    public static java.awt.geom.Rectangle2D rectangleAwtFromExtents( final Extents2D extents ) {
        final double x = extents.getX();
        final double y = extents.getY();
        final double width = extents.getWidth();
        final double height = extents.getHeight();
        final java.awt.geom.Rectangle2D awtRectangle =
                                                     new java.awt.geom.Rectangle2D.Double( x,
                                                                                           y,
                                                                                           width,
                                                                                           height );
        return awtRectangle;
    }

    // Get an AWT rectangle, converted from JavaFX.
    public static java.awt.geom.Rectangle2D rectangleAwtFromRectangle( final Rectangle fxRectangle ) {
        final double x = fxRectangle.getX();
        final double y = fxRectangle.getY();
        final double width = fxRectangle.getWidth();
        final double height = fxRectangle.getHeight();
        final java.awt.geom.Rectangle2D awtRectangle =
                                                     new java.awt.geom.Rectangle2D.Double( x,
                                                                                           y,
                                                                                           width,
                                                                                           height );
        return awtRectangle;
    }

    // Get an AWT rectangle, converted from JavaFX.
    public static java.awt.geom.Rectangle2D rectangleAwtFromRectangle2D( final Bounds fxBounds ) {
        final double x = fxBounds.getMinX();
        final double y = fxBounds.getMinY();
        final double width = fxBounds.getWidth();
        final double height = fxBounds.getHeight();
        final java.awt.geom.Rectangle2D awtRectangle =
                                                     new java.awt.geom.Rectangle2D.Double( x,
                                                                                           y,
                                                                                           width,
                                                                                           height );
        return awtRectangle;
    }

    // Get an AWT rectangle, converted from JavaFX.
    public static java.awt.geom.Rectangle2D rectangleAwtFromRectangle2D( final Rectangle2D fxRectangle ) {
        final double x = fxRectangle.getMinX();
        final double y = fxRectangle.getMinY();
        final double width = fxRectangle.getWidth();
        final double height = fxRectangle.getHeight();
        final java.awt.geom.Rectangle2D awtRectangle =
                                                     new java.awt.geom.Rectangle2D.Double( x,
                                                                                           y,
                                                                                           width,
                                                                                           height );
        return awtRectangle;
    }

    public static Rectangle rectangleFromBounds( final Bounds bounds ) {
        return new Rectangle( bounds.getMinX(),
                              bounds.getMinY(),
                              bounds.getWidth(),
                              bounds.getHeight() );
    }

    public static Rectangle rectangleFromRectangle2D( final Rectangle2D rectangle ) {
        return new Rectangle( rectangle.getMinX(),
                              rectangle.getMinY(),
                              rectangle.getWidth(),
                              rectangle.getHeight() );
    }

    public static Point3D rotateInPlane( final Point3D point3D,
                                         final OrthogonalAxes orthogonalAxes,
                                         final double angleInRadians ) {
        double axis1Value = 0d;
        double axis2Value = 0d;

        switch ( orthogonalAxes ) {
        case XY:
            axis1Value = point3D.getX();
            axis2Value = point3D.getY();
            break;
        case XZ:
            axis1Value = point3D.getX();
            axis2Value = point3D.getZ();
            break;
        case YZ:
            axis1Value = point3D.getY();
            axis2Value = point3D.getZ();
            break;
        default:
            break;
        }

        final double axis1ValueRotated = ( axis1Value * Math.cos( angleInRadians ) )
                - ( axis2Value * Math.sin( angleInRadians ) );

        final double axis2ValueRotated = ( axis1Value * Math.sin( angleInRadians ) )
                + ( axis2Value * Math.cos( angleInRadians ) );

        switch ( orthogonalAxes ) {
        case XY:
            return new Point3D( axis1ValueRotated, axis2ValueRotated, 0d );
        case XZ:
            return new Point3D( axis1ValueRotated, 0d, axis2ValueRotated );
        case YZ:
            return new Point3D( 0d, axis1ValueRotated, axis2ValueRotated );
        default:
            return Point3D.ZERO;
        }
    }

    /**
     * This method transforms a coordinate to its rotated and translated
     * x-axis location, as a partial of a full point transform, when a point
     * object would be an expensive throw-away interim construct.
     *
     * @param x
     *            The x-coordinate of the original untransformed point
     * @param y
     *            The y-coordinate of the original untransformed point
     * @param offsetX
     *            The x-coordinate of the reference point for translation
     * @param theta
     *            The angle by which to rotate the original point
     * @return The x-coordinate of the transformed point
     */
    public static double transformX( final double x,
                                     final double y,
                                     final double offsetX,
                                     final double theta ) {
        final double xTransformed = ( ( x * Math.cos( theta ) ) - ( y * Math.sin( theta ) ) )
                + offsetX;
        return xTransformed;
    }

    /**
     * This method transforms a coordinate to its rotated and translated
     * y-axis location, as a partial of a full point transform, when a point
     * object would be an expensive throw-away interim construct.
     *
     * @param x
     *            The x-coordinate of the original untransformed point
     * @param y
     *            The y-coordinate of the original untransformed point
     * @param offsetY
     *            The y-coordinate of the reference point for translation
     * @param theta
     *            The angle by which to rotate the original point
     * @return The y-coordinate of the transformed point
     */
    public static double transformY( final double x,
                                     final double y,
                                     final double offsetY,
                                     final double theta ) {
        final double yTransformed = ( ( x * Math.sin( theta ) ) + ( y * Math.cos( theta ) ) )
                + offsetY;
        return yTransformed;
    }

    /**
     * Computes the union of one <code>Rectangle</code> with another
     * <code>Rectangle</code>. Returns a new <code>Rectangle</code> that
     * represents the union of the two rectangles.
     * <p>
     * If either {@code Rectangle} has any dimension less than zero the rules
     * for <a href=#NonExistent>non-existent</a> rectangles apply. If only one
     * has a dimension less than zero, then the result will be a copy of the
     * other {@code Rectangle}. If both have dimension less than zero, then the
     * result will have at least one dimension less than zero.
     * <p>
     * If the resulting {@code Rectangle} would have a dimension too large to be
     * expressed as an {@code int}, the result will have a dimension of
     * {@code Integer.MAX_VALUE} along that dimension.
     *
     * NOTE: This method is modified from AWT Rectangle, as the functionality
     * is missing (as far as we can tell) from JavaFX.
     *
     * @param r1
     *            the first <code>Rectangle</code>
     * @param r2
     *            the second <code>Rectangle</code>
     * @return the smallest <code>Rectangle</code> containing both the first
     *         <code>Rectangle</code> and the second <code>Rectangle</code>.
     */
    public static Bounds union( final Bounds r1, final Bounds r2 ) {
        double tx2 = r1.getWidth();
        double ty2 = r1.getHeight();
        if ( ( tx2 < 0d ) || ( ty2 < 0d ) ) {
            // The first rectangle has negative dimensions...
            // If r1 has non-negative dimensions then it is the answer.
            // If r1 is non-existent (has a negative dimension), then both
            // are non-existent and we can return any non-existent rectangle
            // as an answer. Thus, returning r2 meets that criterion.
            // Either way, r2 is our answer.
            return new BoundingBox( r2.getMinX(), r2.getMinY(), r2.getWidth(), r2.getHeight() );
        }

        double rx2 = r2.getWidth();
        double ry2 = r2.getHeight();
        if ( ( rx2 < 0d ) || ( ry2 < 0d ) ) {
            // The second rectangle has negative dimensions...
            // If r2 has non-negative dimensions then it is the answer.
            // If r2 is non-existent (has a negative dimension), then both
            // are non-existent and we can return any non-existent rectangle
            // as an answer. Thus, returning r1 meets that criterion.
            // Either way, r1 is our answer.
            return new BoundingBox( r1.getMinX(), r1.getMinY(), r1.getWidth(), r1.getHeight() );
        }

        double tx1 = r1.getMinX();
        double ty1 = r1.getMinY();
        tx2 += tx1;
        ty2 += ty1;

        final double rx1 = r2.getMinX();
        final double ry1 = r2.getMinY();
        rx2 += rx1;
        ry2 += ry1;

        if ( tx1 > rx1 ) {
            tx1 = rx1;
        }
        if ( ty1 > ry1 ) {
            ty1 = ry1;
        }
        if ( tx2 < rx2 ) {
            tx2 = rx2;
        }
        if ( ty2 < ry2 ) {
            ty2 = ry2;
        }

        tx2 -= tx1;
        ty2 -= ty1;

        // tx2,ty2 will never underflow since both original rectangles
        // were already proven to be non-empty.
        // They might overflow, though...
        if ( tx2 > Double.MAX_VALUE ) {
            tx2 = Double.MAX_VALUE;
        }
        if ( ty2 > Double.MAX_VALUE ) {
            ty2 = Double.MAX_VALUE;
        }

        return new BoundingBox( tx1, ty1, tx2, ty2 );
    }

    /**
     * Computes the union of one <code>Rectangle</code> with another
     * <code>Rectangle</code>. Returns a new <code>Rectangle</code> that
     * represents the union of the two rectangles.
     * <p>
     * If either {@code Rectangle} has any dimension less than zero the rules
     * for <a href=#NonExistent>non-existent</a> rectangles apply. If only one
     * has a dimension less than zero, then the result will be a copy of the
     * other {@code Rectangle}. If both have dimension less than zero, then the
     * result will have at least one dimension less than zero.
     * <p>
     * If the resulting {@code Rectangle} would have a dimension too large to be
     * expressed as an {@code int}, the result will have a dimension of
     * {@code Integer.MAX_VALUE} along that dimension.
     *
     * NOTE: This method is modified from AWT Rectangle, as the functionality
     * is missing (as far as we can tell) from JavaFX.
     *
     * @param r1
     *            the first <code>Rectangle</code>
     * @param r2
     *            the second <code>Rectangle</code>
     * @return the smallest <code>Rectangle</code> containing both the first
     *         <code>Rectangle</code> and the second <code>Rectangle</code>.
     */
    public static java.awt.Rectangle union( final java.awt.geom.Rectangle2D r1,
                                            final java.awt.geom.Rectangle2D r2 ) {
        double tx2 = r1.getWidth();
        double ty2 = r1.getHeight();
        if ( ( tx2 < 0.0 ) || ( ty2 < 0.0 ) ) {
            // The first rectangle has negative dimensions...
            // If r1 has non-negative dimensions then it is the answer.
            // If r1 is non-existent (has a negative dimension), then both
            // are non-existent and we can return any non-existent rectangle
            // as an answer. Thus, returning r2 meets that criterion.
            // Either way, r2 is our answer.
            return new java.awt.Rectangle( ( int ) Math.round( r2.getMinX() ),
                                           ( int ) Math.round( r2.getMinY() ),
                                           ( int ) Math.round( r2.getWidth() ),
                                           ( int ) Math.round( r2.getHeight() ) );
        }

        double rx2 = r2.getWidth();
        double ry2 = r2.getHeight();
        if ( ( rx2 < 0.0 ) || ( ry2 < 0.0 ) ) {
            // The second rectangle has negative dimensions...
            // If r2 has non-negative dimensions then it is the answer.
            // If r2 is non-existent (has a negative dimension), then both
            // are non-existent and we can return any non-existent rectangle
            // as an answer. Thus, returning r1 meets that criterion.
            // Either way, r1 is our answer.
            return new java.awt.Rectangle( ( int ) Math.round( r1.getMinX() ),
                                           ( int ) Math.round( r1.getMinY() ),
                                           ( int ) Math.round( r1.getWidth() ),
                                           ( int ) Math.round( r1.getHeight() ) );
        }

        double tx1 = r1.getMinX();
        double ty1 = r1.getMinY();
        tx2 += tx1;
        ty2 += ty1;

        final double rx1 = r2.getMinX();
        final double ry1 = r2.getMinY();
        rx2 += rx1;
        ry2 += ry1;

        if ( tx1 > rx1 ) {
            tx1 = rx1;
        }
        if ( ty1 > ry1 ) {
            ty1 = ry1;
        }
        if ( tx2 < rx2 ) {
            tx2 = rx2;
        }
        if ( ty2 < ry2 ) {
            ty2 = ry2;
        }

        tx2 -= tx1;
        ty2 -= ty1;

        // tx2,ty2 will never underflow since both original rectangles
        // were already proven to be non-empty.
        // They might overflow, though...
        if ( tx2 > Double.MAX_VALUE ) {
            tx2 = Double.MAX_VALUE;
        }
        if ( ty2 > Double.MAX_VALUE ) {
            ty2 = Double.MAX_VALUE;
        }

        return new java.awt.Rectangle( ( int ) Math.round( tx1 ),
                                       ( int ) Math.round( ty1 ),
                                       ( int ) Math.round( tx2 ),
                                       ( int ) Math.round( ty2 ) );
    }

    /**
     * Computes the union of one <code>Rectangle</code> with another
     * <code>Rectangle</code>. Returns a new <code>Rectangle</code> that
     * represents the union of the two rectangles.
     * <p>
     * If either {@code Rectangle} has any dimension less than zero the rules
     * for <a href=#NonExistent>non-existent</a> rectangles apply. If only one
     * has a dimension less than zero, then the result will be a copy of the
     * other {@code Rectangle}. If both have dimension less than zero, then the
     * result will have at least one dimension less than zero.
     * <p>
     * If the resulting {@code Rectangle} would have a dimension too large to be
     * expressed as an {@code int}, the result will have a dimension of
     * {@code Integer.MAX_VALUE} along that dimension.
     *
     * NOTE: This method is modified from AWT Rectangle, as the functionality
     * is missing (as far as we can tell) from JavaFX.
     *
     * @param r1
     *            the first <code>Rectangle</code>
     * @param r2
     *            the second <code>Rectangle</code>
     * @return the smallest <code>Rectangle</code> containing both the first
     *         <code>Rectangle</code> and the second <code>Rectangle</code>.
     */
    public static Rectangle2D union( final Rectangle2D r1, final Rectangle2D r2 ) {
        double tx2 = r1.getWidth();
        double ty2 = r1.getHeight();
        if ( ( tx2 < 0.0 ) || ( ty2 < 0.0 ) ) {
            // The first rectangle has negative dimensions...
            // If r1 has non-negative dimensions then it is the answer.
            // If r1 is non-existent (has a negative dimension), then both
            // are non-existent and we can return any non-existent rectangle
            // as an answer. Thus, returning r2 meets that criterion.
            // Either way, r2 is our answer.
            return new Rectangle2D( r2.getMinX(), r2.getMinY(), r2.getWidth(), r2.getHeight() );
        }

        double rx2 = r2.getWidth();
        double ry2 = r2.getHeight();
        if ( ( rx2 < 0.0 ) || ( ry2 < 0.0 ) ) {
            // The second rectangle has negative dimensions...
            // If r2 has non-negative dimensions then it is the answer.
            // If r2 is non-existent (has a negative dimension), then both
            // are non-existent and we can return any non-existent rectangle
            // as an answer. Thus, returning r1 meets that criterion.
            // Either way, r1 is our answer.
            return new Rectangle2D( r1.getMinX(), r1.getMinY(), r1.getWidth(), r1.getHeight() );
        }

        double tx1 = r1.getMinX();
        double ty1 = r1.getMinY();
        tx2 += tx1;
        ty2 += ty1;

        final double rx1 = r2.getMinX();
        final double ry1 = r2.getMinY();
        rx2 += rx1;
        ry2 += ry1;

        if ( tx1 > rx1 ) {
            tx1 = rx1;
        }
        if ( ty1 > ry1 ) {
            ty1 = ry1;
        }
        if ( tx2 < rx2 ) {
            tx2 = rx2;
        }
        if ( ty2 < ry2 ) {
            ty2 = ry2;
        }

        tx2 -= tx1;
        ty2 -= ty1;

        // tx2,ty2 will never underflow since both original rectangles
        // were already proven to be non-empty.
        // They might overflow, though...
        if ( tx2 > Double.MAX_VALUE ) {
            tx2 = Double.MAX_VALUE;
        }
        if ( ty2 > Double.MAX_VALUE ) {
            ty2 = Double.MAX_VALUE;
        }

        return new Rectangle2D( tx1, ty1, tx2, ty2 );
    }

    /**
     * Updates a given Bounding Box to contain a supplied Point.
     * <p>
     * If the Bounding Box has any dimension less than zero the rules for
     * <a href=#NonExistent>non-existent</a> rectangles apply.
     * <p>
     * If the resulting {@code Rectangle} would have a dimension too large to be
     * expressed as an {@code int}, the result will have a dimension of
     * {@code Integer.MAX_VALUE} along that dimension.
     *
     * @param bbox
     *            The current Bounding Box, unmodified
     * @param point
     *            The point to use for expanding the Bounding Box
     * @return The smallest Bounding Box containing both the original Bounding
     *         Box and the supplied Point.
     */
    public static Bounds updateBounds( final Bounds bbox, final Point2D point ) {
        double width = bbox.getWidth();
        double height = bbox.getHeight();
        if ( ( width < 0d ) || ( height < 0d ) ) {
            // The original Bounding Box has negative dimensions...
            // If bbox is non-existent (has a negative dimension), then it has
            // not been initialized yet, so we use the supplied Point to do
            // that, with zero dimensions.
            return new BoundingBox( point.getX(), point.getY(), 0d, 0d );
        }

        double minX = bbox.getMinX();
        double minY = bbox.getMinY();
        double maxX = minX + width;
        double maxY = minY + height;

        final double x = point.getX();
        final double y = point.getY();

        if ( minX > x ) {
            minX = x;
        }
        if ( minY > y ) {
            minY = y;
        }

        if ( maxX < x ) {
            maxX = x;
        }
        if ( maxY < y ) {
            maxY = y;
        }

        width = maxX - minX;
        height = maxY - minY;

        // width,height will never underflow since the original Bounding Box was
        // already proven to be non-empty.
        // They might overflow, though...
        if ( width > Double.MAX_VALUE ) {
            width = Double.MAX_VALUE;
        }
        if ( height > Double.MAX_VALUE ) {
            height = Double.MAX_VALUE;
        }

        return new BoundingBox( minX, minY, width, height );
    }

}
