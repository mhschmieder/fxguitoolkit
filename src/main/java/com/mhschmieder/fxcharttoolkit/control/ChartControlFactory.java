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
 * FxGuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxcharttoolkit.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.mhschmieder.commonstoolkit.math.MathExt;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

public class ChartControlFactory {

    /**
     * The default constructor is disabled, as this is a static factory class.
     */
    private ChartControlFactory() {}

    // TODO: Find a way to combine the layoutChildren() override with the Line
    // Chart as it is identical -- perhaps a functional programming grammar will
    // work?
    public static final AreaChart< Number, Number > getNumberAreaChart( final ValueAxis< Number > xAxis,
                                                                        final ValueAxis< Number > yAxis ) {
        // NOTE: Data point symbols are unnecessary and also non-performant.
        final AreaChart< Number, Number > numberAreaChart =
                                                          new AreaChart< Number, Number >( xAxis,
                                                                                           yAxis ) {
                                                              /**
                                                               * Invoked during
                                                               * the layout pass
                                                               * to layout this
                                                               * chart and all
                                                               * its
                                                               * content.
                                                               */
                                                              @Override
                                                              protected void layoutChildren() {
                                                                  // Have FX
                                                                  // Charts do
                                                                  // the main
                                                                  // layout; we
                                                                  // only need
                                                                  // to override
                                                                  // some
                                                                  // undesirable
                                                                  // non-configurable
                                                                  // legend
                                                                  // positioning.
                                                                  super.layoutChildren();

                                                                  final double top =
                                                                                   snappedTopInset();
                                                                  final double left =
                                                                                    snappedLeftInset();
                                                                  final double bottom =
                                                                                      snappedBottomInset();
                                                                  final double right =
                                                                                     snappedRightInset();
                                                                  final double width = getWidth();
                                                                  final double height = getHeight();

                                                                  // Layout the
                                                                  // legend
                                                                  // anew, if
                                                                  // left or
                                                                  // right side,
                                                                  // as we want
                                                                  // top-justification
                                                                  // rather than
                                                                  // vertically
                                                                  // centered
                                                                  // legends.
                                                                  // TODO:
                                                                  // Replace
                                                                  // snapSize()
                                                                  // with JavaFX
                                                                  // 9's
                                                                  // snapSizeX().
                                                                  final Node legend = getLegend();
                                                                  if ( legend != null ) {
                                                                      final boolean shouldShowLegend =
                                                                                                     isLegendVisible();
                                                                      double legendWidth = 0d;
                                                                      double legendHeight = 0d;
                                                                      if ( shouldShowLegend ) {
                                                                          switch ( getLegendSide() ) {
                                                                          case BOTTOM:
                                                                              break;
                                                                          case LEFT:
                                                                              legendWidth =
                                                                                          snapSize( legend
                                                                                                  .prefWidth( height
                                                                                                          - top
                                                                                                          - bottom ) );
                                                                              legendHeight = MathExt
                                                                                      .boundedValue( snapSize( legend
                                                                                              .prefHeight( legendWidth ) ),
                                                                                                     0d,
                                                                                                     height - top
                                                                                                             - bottom );
                                                                              legend.resizeRelocate( left,
                                                                                                     top,
                                                                                                     legendWidth,
                                                                                                     legendHeight );
                                                                              break;
                                                                          case RIGHT:
                                                                              legendWidth =
                                                                                          snapSize( legend
                                                                                                  .prefWidth( height
                                                                                                          - top
                                                                                                          - bottom ) );
                                                                              legendHeight = MathExt
                                                                                      .boundedValue( snapSize( legend
                                                                                              .prefHeight( legendWidth ) ),
                                                                                                     0d,
                                                                                                     height - top
                                                                                                             - bottom );
                                                                              legend.resizeRelocate( width
                                                                                      - right
                                                                                      - legendWidth,
                                                                                                     top,
                                                                                                     legendWidth,
                                                                                                     legendHeight );
                                                                              break;
                                                                          case TOP:
                                                                              break;
                                                                          default:
                                                                              break;
                                                                          }
                                                                      }
                                                                      legend.setVisible( shouldShowLegend );
                                                                  }
                                                              }

                                                              @Override
                                                              protected void layoutPlotChildren() {
                                                                  // NOTE: We
                                                                  // can't
                                                                  // override
                                                                  // getDataSize(),
                                                                  // so we
                                                                  // replicate
                                                                  // its
                                                                  // logic here
                                                                  // instead.
                                                                  // final int
                                                                  // dataSize =
                                                                  // getDataSize();
                                                                  final ObservableList< Series< Number, Number > > data =
                                                                                                                        getData();
                                                                  final int dataSize =
                                                                                     ( data != null )
                                                                                         ? data.size()
                                                                                         : 0;

                                                                  final ArrayList< LineTo > constructedPath =
                                                                                                            new ArrayList<>( dataSize );
                                                                  for ( int seriesIndex =
                                                                                        0; seriesIndex < dataSize; seriesIndex++ ) {
                                                                      final Series< Number, Number > series =
                                                                                                            getData()
                                                                                                                    .get( seriesIndex );

                                                                      // NOTE:
                                                                      // This
                                                                      // property
                                                                      // is
                                                                      // private
                                                                      // and
                                                                      // can't
                                                                      // be seen
                                                                      // in
                                                                      // overridden
                                                                      // methods,
                                                                      // but is
                                                                      // usually
                                                                      // zero or
                                                                      // one
                                                                      // based
                                                                      // on
                                                                      // animation
                                                                      // status.
                                                                      // final
                                                                      // DoubleProperty
                                                                      // seriesYAnimMultiplier
                                                                      // =
                                                                      // seriesYMultiplierMap.get(
                                                                      // series
                                                                      // );
                                                                      final DoubleProperty seriesYAnimMultiplier =
                                                                                                                 new SimpleDoubleProperty( 1d );

                                                                      final ObservableList< Node > children =
                                                                                                            ( ( Group ) series
                                                                                                                    .getNode() )
                                                                                                                            .getChildren();
                                                                      final ObservableList< PathElement > seriesLine =
                                                                                                                     ( ( Path ) children
                                                                                                                             .get( 1 ) )
                                                                                                                                     .getElements();
                                                                      final ObservableList< PathElement > fillPath =
                                                                                                                   ( ( Path ) children
                                                                                                                           .get( 0 ) )
                                                                                                                                   .getElements();

                                                                      // Construct
                                                                      // the set
                                                                      // of line
                                                                      // actions
                                                                      // between
                                                                      // data
                                                                      // points.
                                                                      seriesLine.clear();
                                                                      fillPath.clear();
                                                                      constructedPath.clear();
                                                                      for ( final Iterator< Data< Number, Number > > it =
                                                                                                                        getDisplayedDataIterator( series ); it
                                                                                                                                .hasNext(); ) {
                                                                          final Data< Number, Number > item =
                                                                                                            it.next();

                                                                          // NOTE:
                                                                          // We
                                                                          // have
                                                                          // to
                                                                          // go
                                                                          // about
                                                                          // this
                                                                          // a
                                                                          // roundabout
                                                                          // way,
                                                                          // as
                                                                          // the
                                                                          // original
                                                                          // methods
                                                                          // invoked
                                                                          // from
                                                                          // Oracle's
                                                                          // source
                                                                          // code
                                                                          // are
                                                                          // private
                                                                          // and
                                                                          // not
                                                                          // available
                                                                          // in
                                                                          // overrides.
                                                                          // final
                                                                          // Number
                                                                          // xValue
                                                                          // =
                                                                          // item.getCurrentX();
                                                                          // final
                                                                          // Number
                                                                          // yValue
                                                                          // =
                                                                          // item.getCurrentY();
                                                                          final Number xValue = item
                                                                                  .getXValue();
                                                                          final Number yValue = item
                                                                                  .getYValue();
                                                                          final double x =
                                                                                         getXAxis()
                                                                                                 .getDisplayPosition( xValue );
                                                                          final double y =
                                                                                         getYAxis()
                                                                                                 .getDisplayPosition( getYAxis()
                                                                                                         .toRealValue( getYAxis()
                                                                                                                 .toNumericValue( yValue )
                                                                                                                 * seriesYAnimMultiplier
                                                                                                                         .getValue() ) );

                                                                          constructedPath
                                                                                  .add( new LineTo( x,
                                                                                                    y ) );
                                                                          if ( Double.isNaN( x )
                                                                                  || Double
                                                                                          .isNaN( y ) ) {
                                                                              continue;
                                                                          }
                                                                          final Node symbol = item
                                                                                  .getNode();
                                                                          if ( symbol != null ) {
                                                                              final double w =
                                                                                             symbol.prefWidth( -1 );
                                                                              final double h =
                                                                                             symbol.prefHeight( -1 );
                                                                              symbol.resizeRelocate( x
                                                                                      - ( w / 2 ),
                                                                                                     y - ( h / 2 ),
                                                                                                     w,
                                                                                                     h );
                                                                          }
                                                                      }

                                                                      // Use the
                                                                      // bar
                                                                      // lines
                                                                      // as a
                                                                      // basis
                                                                      // for the
                                                                      // area
                                                                      // fill
                                                                      // paths.
                                                                      // NOTE:
                                                                      // This
                                                                      // also
                                                                      // sorts
                                                                      // the
                                                                      // line
                                                                      // actions
                                                                      // for
                                                                      // x-axis
                                                                      // order.
                                                                      if ( !constructedPath
                                                                              .isEmpty() ) {
                                                                          // Sort
                                                                          // the
                                                                          // line
                                                                          // actions
                                                                          // by
                                                                          // increasing
                                                                          // x-axis
                                                                          // order.
                                                                          Collections
                                                                                  .sort( constructedPath,
                                                                                         ( e1,
                                                                                           e2 ) -> Double
                                                                                                   .compare( e1
                                                                                                           .getX(),
                                                                                                             e2.getX() ) );

                                                                          final LineTo first =
                                                                                             constructedPath
                                                                                                     .get( 0 );
                                                                          final LineTo last =
                                                                                            constructedPath
                                                                                                    .get( constructedPath
                                                                                                            .size()
                                                                                                            - 1 );

                                                                          final double firstX =
                                                                                              first.getX();
                                                                          final double firstY =
                                                                                              first.getY();
                                                                          final double lastX = last
                                                                                  .getX();

                                                                          // RT-34626:
                                                                          // We
                                                                          // can't
                                                                          // always
                                                                          // use
                                                                          // getZeroPosition(),
                                                                          // as
                                                                          // it
                                                                          // may
                                                                          // be
                                                                          // the
                                                                          // case
                                                                          // that
                                                                          // the
                                                                          // zero
                                                                          // position
                                                                          // of
                                                                          // the
                                                                          // y-axis
                                                                          // is
                                                                          // not
                                                                          // visible
                                                                          // on
                                                                          // the
                                                                          // chart.
                                                                          // In
                                                                          // these
                                                                          // cases,
                                                                          // we
                                                                          // need
                                                                          // to
                                                                          // use
                                                                          // the
                                                                          // height
                                                                          // between
                                                                          // the
                                                                          // point
                                                                          // and
                                                                          // the
                                                                          // y-axis
                                                                          // line.
                                                                          // NOTE:
                                                                          // Commented
                                                                          // out,
                                                                          // because
                                                                          // we
                                                                          // don't
                                                                          // want
                                                                          // this
                                                                          // behavior,
                                                                          // and
                                                                          // it
                                                                          // also
                                                                          // causes
                                                                          // downstream
                                                                          // anomalies
                                                                          // with
                                                                          // non-manifold
                                                                          // topologies
                                                                          // for
                                                                          // the
                                                                          // overall
                                                                          // area
                                                                          // fill
                                                                          // closure.
                                                                          // final
                                                                          // double
                                                                          // numericYPos
                                                                          // =
                                                                          // getYAxis().toNumericValue(
                                                                          // getYAxis().getValueForDisplay(
                                                                          // firstY
                                                                          // )
                                                                          // );
                                                                          // final
                                                                          // double
                                                                          // yAxisZeroPos
                                                                          // =
                                                                          // getYAxis().getZeroPosition();
                                                                          // final
                                                                          // boolean
                                                                          // isYAxisZeroPosVisible
                                                                          // =
                                                                          // !Double.isNaN(
                                                                          // yAxisZeroPos
                                                                          // );
                                                                          // final
                                                                          // double
                                                                          // yAxisHeight
                                                                          // =
                                                                          // getYAxis().getHeight();
                                                                          // final
                                                                          // double
                                                                          // yFillPos
                                                                          // =
                                                                          // isYAxisZeroPosVisible
                                                                          // ?
                                                                          // yAxisZeroPos
                                                                          // :
                                                                          // numericYPos
                                                                          // < 0
                                                                          // ?
                                                                          // numericYPos
                                                                          // -
                                                                          // yAxisHeight
                                                                          // :
                                                                          // yAxisHeight;

                                                                          // NOTE:
                                                                          // This
                                                                          // is
                                                                          // done
                                                                          // in
                                                                          // place
                                                                          // of
                                                                          // using
                                                                          // the
                                                                          // y-axis
                                                                          // zero
                                                                          // position,
                                                                          // as
                                                                          // we
                                                                          // do
                                                                          // not
                                                                          // want
                                                                          // to
                                                                          // truncate
                                                                          // down-fills
                                                                          // for
                                                                          // positive
                                                                          // y-axis
                                                                          // values
                                                                          // or
                                                                          // use
                                                                          // up-fills
                                                                          // for
                                                                          // negative
                                                                          // y-axis
                                                                          // values.
                                                                          // We
                                                                          // fill
                                                                          // to
                                                                          // the
                                                                          // minimum
                                                                          // value.
                                                                          // :NOTE:
                                                                          // As
                                                                          // we
                                                                          // are
                                                                          // comparing
                                                                          // screen
                                                                          // coordinates,
                                                                          // the
                                                                          // smallest
                                                                          // y-axis
                                                                          // value
                                                                          // is
                                                                          // actually
                                                                          // the
                                                                          // largest
                                                                          // pixel
                                                                          // offset
                                                                          // from
                                                                          // the
                                                                          // layout
                                                                          // container's
                                                                          // origin.
                                                                          // NOTE:
                                                                          // Even
                                                                          // so,
                                                                          // we
                                                                          // want
                                                                          // to
                                                                          // fill
                                                                          // to
                                                                          // the
                                                                          // bottom
                                                                          // of
                                                                          // the
                                                                          // chart
                                                                          // if
                                                                          // that
                                                                          // is
                                                                          // lower
                                                                          // than
                                                                          // the
                                                                          // lowest
                                                                          // y-axis
                                                                          // value.
                                                                          final double lowestYValue =
                                                                                                    Collections
                                                                                                            .max( constructedPath,
                                                                                                                  ( e1,
                                                                                                                    e2 ) -> Double
                                                                                                                            .compare( e1
                                                                                                                                    .getY(),
                                                                                                                                      e2.getY() ) )
                                                                                                            .getY();
                                                                          final double yAxisMin =
                                                                                                getYAxis()
                                                                                                        .getDisplayPosition( ( ( ValueAxis< Number > ) getYAxis() )
                                                                                                                .getLowerBound() );
                                                                          final double yFillPos =
                                                                                                Double.isFinite( lowestYValue )
                                                                                                    ? Math.max( lowestYValue,
                                                                                                                yAxisMin )
                                                                                                    : yAxisMin;

                                                                          // The
                                                                          // line
                                                                          // series
                                                                          // is
                                                                          // just
                                                                          // the
                                                                          // sorted
                                                                          // line
                                                                          // actions
                                                                          // (path
                                                                          // elements),
                                                                          // with
                                                                          // a
                                                                          // move
                                                                          // action
                                                                          // to
                                                                          // the
                                                                          // first
                                                                          // data
                                                                          // point
                                                                          // preceding
                                                                          // the
                                                                          // line
                                                                          // actions,
                                                                          // as
                                                                          // otherwise
                                                                          // the
                                                                          // first
                                                                          // bar
                                                                          // line
                                                                          // isn't
                                                                          // drawn.
                                                                          seriesLine
                                                                                  .add( new MoveTo( firstX,
                                                                                                    firstY ) );
                                                                          seriesLine
                                                                                  .addAll( constructedPath );

                                                                          // The
                                                                          // area
                                                                          // fill
                                                                          // series
                                                                          // is
                                                                          // the
                                                                          // sorted
                                                                          // line
                                                                          // actions,
                                                                          // plus
                                                                          // a
                                                                          // move
                                                                          // command
                                                                          // at
                                                                          // either
                                                                          // end
                                                                          // and
                                                                          // a
                                                                          // path
                                                                          // closure
                                                                          // to
                                                                          // connect
                                                                          // the
                                                                          // beginning
                                                                          // and
                                                                          // end
                                                                          // move
                                                                          // points.
                                                                          // NOTE:
                                                                          // This
                                                                          // code
                                                                          // uses
                                                                          // a
                                                                          // single
                                                                          // area
                                                                          // fill,
                                                                          // instead
                                                                          // of
                                                                          // individual
                                                                          // bar
                                                                          // fills,
                                                                          // and
                                                                          // therefore
                                                                          // can
                                                                          // potentially
                                                                          // cause
                                                                          // anomalies
                                                                          // if
                                                                          // the
                                                                          // resulting
                                                                          // closure
                                                                          // is
                                                                          // a
                                                                          // non-manifold.
                                                                          // Hopefully
                                                                          // we
                                                                          // accounted
                                                                          // for
                                                                          // this
                                                                          // above.
                                                                          fillPath.add( new MoveTo( firstX,
                                                                                                    yFillPos ) );
                                                                          fillPath.addAll( constructedPath );
                                                                          fillPath.add( new LineTo( lastX,
                                                                                                    yFillPos ) );
                                                                          fillPath.add( new ClosePath() );
                                                                      }
                                                                  }
                                                              }

                                                              /**
                                                               * This is called
                                                               * whenever a
                                                               * series is added
                                                               * or removed and
                                                               * the
                                                               * legend needs to
                                                               * be updated. We
                                                               * do manual
                                                               * legends as the
                                                               * ones in
                                                               * FX Charts are
                                                               * neither
                                                               * flexible in
                                                               * layout details
                                                               * nor do they
                                                               * expose
                                                               * important
                                                               * parameters in
                                                               * Public API.
                                                               *
                                                               * TODO: Review
                                                               * whether this is
                                                               * still needed,
                                                               * either as a
                                                               * do-nothing
                                                               * override in
                                                               * place of the
                                                               * default
                                                               * implementation,
                                                               * or to
                                                               * nullify the
                                                               * Legend
                                                               * reference.
                                                               */
                                                              @Override
                                                              protected void updateLegend() {
                                                                  // Try to
                                                                  // avoid side
                                                                  // effects on
                                                                  // bindings
                                                                  // and bounds,
                                                                  // if legend
                                                                  // is present
                                                                  // but simply
                                                                  // empty or
                                                                  // not shown,
                                                                  // by
                                                                  // invalidating
                                                                  // its
                                                                  // reference
                                                                  // so that its
                                                                  // on-screen
                                                                  // space isn't
                                                                  // computed.
                                                                  setLegend( null );
                                                              }
                                                          };

        return numberAreaChart;
    }

    // TODO: Find a way to combine the layoutChildren() override with the Area
    // Chart as it is identical -- perhaps a functional programming grammar will
    // work?
    public static final LineChart< Number, Number > getNumberLineChart( final ValueAxis< Number > xAxis,
                                                                        final ValueAxis< Number > yAxis ) {
        // NOTE: Data point symbols are unnecessary and also non-performant.
        final LineChart< Number, Number > numberLineChart =
                                                          new LineChart< Number, Number >( xAxis,
                                                                                           yAxis ) {
                                                              /**
                                                               * Invoked during
                                                               * the layout pass
                                                               * to layout this
                                                               * chart and all
                                                               * its
                                                               * content.
                                                               */
                                                              @Override
                                                              protected void layoutChildren() {
                                                                  // Have FX
                                                                  // Charts do
                                                                  // the main
                                                                  // layout; we
                                                                  // only need
                                                                  // to override
                                                                  // some
                                                                  // undesirable
                                                                  // non-configurable
                                                                  // legend
                                                                  // positioning.
                                                                  super.layoutChildren();

                                                                  final double top =
                                                                                   snappedTopInset();
                                                                  final double left =
                                                                                    snappedLeftInset();
                                                                  final double bottom =
                                                                                      snappedBottomInset();
                                                                  final double right =
                                                                                     snappedRightInset();
                                                                  final double width = getWidth();
                                                                  final double height = getHeight();

                                                                  // Layout the
                                                                  // legend
                                                                  // anew, if
                                                                  // left or
                                                                  // right side,
                                                                  // as we want
                                                                  // top-justification
                                                                  // rather than
                                                                  // vertically
                                                                  // centered
                                                                  // legends.
                                                                  // TODO:
                                                                  // Replace
                                                                  // snapSize()
                                                                  // with JavaFX
                                                                  // 9's
                                                                  // snapSizeX().
                                                                  final Node legend = getLegend();
                                                                  if ( legend != null ) {
                                                                      final boolean shouldShowLegend =
                                                                                                     isLegendVisible();
                                                                      double legendWidth = 0d;
                                                                      double legendHeight = 0d;
                                                                      if ( shouldShowLegend ) {
                                                                          switch ( getLegendSide() ) {
                                                                          case BOTTOM:
                                                                              break;
                                                                          case LEFT:
                                                                              legendWidth =
                                                                                          snapSize( legend
                                                                                                  .prefWidth( height
                                                                                                          - top
                                                                                                          - bottom ) );
                                                                              legendHeight = MathExt
                                                                                      .boundedValue( snapSize( legend
                                                                                              .prefHeight( legendWidth ) ),
                                                                                                     0d,
                                                                                                     height - top
                                                                                                             - bottom );
                                                                              legend.resizeRelocate( left,
                                                                                                     top,
                                                                                                     legendWidth,
                                                                                                     legendHeight );
                                                                              break;
                                                                          case RIGHT:
                                                                              legendWidth =
                                                                                          snapSize( legend
                                                                                                  .prefWidth( height
                                                                                                          - top
                                                                                                          - bottom ) );
                                                                              legendHeight = MathExt
                                                                                      .boundedValue( snapSize( legend
                                                                                              .prefHeight( legendWidth ) ),
                                                                                                     0d,
                                                                                                     height - top
                                                                                                             - bottom );
                                                                              legend.resizeRelocate( width
                                                                                      - right
                                                                                      - legendWidth,
                                                                                                     top,
                                                                                                     legendWidth,
                                                                                                     legendHeight );
                                                                              break;
                                                                          case TOP:
                                                                              break;
                                                                          default:
                                                                              break;
                                                                          }
                                                                      }
                                                                      legend.setVisible( shouldShowLegend );
                                                                  }
                                                              }

                                                              /**
                                                               * This is called
                                                               * whenever a
                                                               * series is added
                                                               * or removed and
                                                               * the
                                                               * legend needs to
                                                               * be updated. We
                                                               * do manual
                                                               * legends as the
                                                               * ones in
                                                               * FX Charts are
                                                               * neither
                                                               * flexible in
                                                               * layout details
                                                               * nor do they
                                                               * expose
                                                               * important
                                                               * parameters in
                                                               * Public API.
                                                               *
                                                               * TODO: Review
                                                               * whether this is
                                                               * still needed,
                                                               * either as a
                                                               * do-nothing
                                                               * override in
                                                               * place of the
                                                               * default
                                                               * implementation,
                                                               * or to
                                                               * nullify the
                                                               * Legend
                                                               * reference.
                                                               */
                                                              @Override
                                                              protected void updateLegend() {
                                                                  // Try to
                                                                  // avoid side
                                                                  // effects on
                                                                  // bindings
                                                                  // and bounds,
                                                                  // if legend
                                                                  // is present
                                                                  // but simply
                                                                  // empty or
                                                                  // not shown,
                                                                  // by
                                                                  // invalidating
                                                                  // its
                                                                  // reference
                                                                  // so that its
                                                                  // on-screen
                                                                  // space isn't
                                                                  // computed.
                                                                  setLegend( null );
                                                              }
                                                          };

        return numberLineChart;
    }

}
