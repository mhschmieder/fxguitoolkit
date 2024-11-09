/**
 * MIT License
 *
 * Copyright (c) 2020, 2024 Mark Schmieder
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
package com.mhschmieder.fxguitoolkit.action;

import java.util.Collection;

import org.controlsfx.control.action.Action;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.control.LabeledControlFactory;

public class LabeledActionFactory {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private LabeledActionFactory() {}

    // NOTE: We must substitute "." for resource directory tree delimiters.
    @SuppressWarnings("nls") public static final String BUNDLE_NAME =
                                                                    "properties.CommonActionLabels";

    // TODO: Load an icon that is a stylized representation of the MRU number.
    @SuppressWarnings("nls")
    public static XAction makeFileMruAction( final ClientProperties pClientProperties,
                                             final int mruFileNumber ) {
        // Make sure the MRU File items self-hide if empty and disabled.
        final String fileMruNumber = "mru" + Integer.toString( mruFileNumber );
        return ActionFactory
                .makeAction( pClientProperties, BUNDLE_NAME, "file", fileMruNumber, null, true );
    }

    @SuppressWarnings("nls")
    public static XActionGroup makeWindowSizeActionGroup( final ClientProperties pClientProperties,
                                                          final WindowSizeActions windowSizeActions,
                                                          final boolean maximumSizeSupported ) {
        final Collection< Action > windowSizeActionCollection = windowSizeActions
                .getWindowSizeActionCollection( maximumSizeSupported );

        final XActionGroup windowSizeActionGroup = ActionFactory
                .makeActionGroup( pClientProperties,
                                  windowSizeActionCollection,
                                  BUNDLE_NAME,
                                  "windowSize",
                                  "/icons/deviantArt/FullScreen16.png" );

        return windowSizeActionGroup;
    }

    @SuppressWarnings("nls")
    public static XAction makeWindowSizeDefaultSizeAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "windowSize",
                                         "defaultSize",
                                         "/icons/yusukeKamiyamane/ApplicationResizeActual16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction makeWindowSizeMaximumSizeAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "windowSize",
                                         "maximumSize",
                                         "/icons/yusukeKamiyamane/ApplicationResizeFull16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction makeWindowSizePreferredSizeAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "windowSize",
                                         "preferredSize",
                                         "/icons/yusukeKamiyamane/ApplicationResize16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction makeScrollingCoarseChoice( final ClientProperties clientProperties ) {
        return ActionFactory
                .makeChoice( clientProperties, BUNDLE_NAME, "scrollingSensitivity", "coarse", null );
    }

    @SuppressWarnings("nls")
    public static XAction makeScrollingFineChoice( final ClientProperties clientProperties ) {
        return ActionFactory
                .makeChoice( clientProperties, BUNDLE_NAME, "scrollingSensitivity", "fine", null );
    }

    @SuppressWarnings("nls")
    public static XAction makeScrollingMediumChoice( final ClientProperties clientProperties ) {
        return ActionFactory
                .makeChoice( clientProperties, BUNDLE_NAME, "scrollingSensitivity", "medium", null );
    }

    @SuppressWarnings("nls")
    public static XAction makeScrollingOffChoice( final ClientProperties sessionContext ) {
        return ActionFactory
                .makeChoice( sessionContext, BUNDLE_NAME, "scrollingSensitivity", "off", null );
    }

    @SuppressWarnings("nls")
    public static XActionGroup makeScrollingSensitivityChoiceGroup( final ClientProperties clientProperties,
                                                                    final ScrollingSensitivityChoices scrollingSensitivityChoices ) {
        final Collection< Action > scrollingSensitivityChoiceCollection =
                                                                        scrollingSensitivityChoices
                                                                                .getScrollingSensitivityChoiceCollection();

        final XActionGroup scrollingSensitivityChoiceGroup = ActionFactory
                .makeChoiceGroup( clientProperties,
                                  scrollingSensitivityChoiceCollection,
                                  BUNDLE_NAME,
                                  "scrollingSensitivity",
                                  "/icons/fatCow/MouseSelectScroll16.png" );

        return scrollingSensitivityChoiceGroup;
    }

    @SuppressWarnings("nls")
    public static XAction makeAnimateChartUpdatesCheck( final ClientProperties pClientProperties ) {
        return ActionFactory.makeCheck( pClientProperties,
                                        LabeledActionFactory.BUNDLE_NAME,
                                        "view",
                                        "animateChartUpdates",
                                        null,
                                        true );
    }

    @SuppressWarnings("nls")
    public static XAction getClearAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         LabeledActionFactory.BUNDLE_NAME,
                                         "tools",
                                         "clear",
                                         "/icons/ahaSoft/Clear16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getPredictAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         LabeledActionFactory.BUNDLE_NAME,
                                         "tools",
                                         "predict",
                                         "/icons/glyphish/Calculator16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getResetAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties, LabeledActionFactory.BUNDLE_NAME, "settings", "reset", null );
    }

    @SuppressWarnings("nls")
    public static XActionGroup getEditActionGroup( final ClientProperties pClientProperties,
                                                   final EditActions editActions ) {
        final Collection< Action > editActionCollection = editActions
                .getEditActionCollection( pClientProperties );

        final XActionGroup editActionGroup = ActionFactory
                .makeActionGroup( pClientProperties, editActionCollection, BUNDLE_NAME, "edit", null );

        return editActionGroup;
    }

    @SuppressWarnings("nls")
    public static XAction getEditCancelPasteAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "edit",
                                         "cancelPaste",
                                         "/icons/ahaSoft/Cancel16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getEditClearImportedGraphicsAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "edit",
                                         "clearImportedGraphics",
                                         "/icons/oxygenIcons/EditClear16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getEditCopyAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "edit",
                                         "copy",
                                         "/icons/oxygenIcons/EditCopy16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getEditCutAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "edit",
                                         "cut",
                                         "/icons/oxygenIcons/EditCut16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getEditDeleteAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "edit",
                                         "delete",
                                         "/icons/oxygenIcons/EditDelete16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getEditDeselectAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties, BUNDLE_NAME, "edit", "deselect", null );
    }

    @SuppressWarnings("nls")
    public static XAction getEditPasteAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "edit",
                                         "paste",
                                         "/icons/oxygenIcons/EditPaste16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getEditPropertiesAction( final ClientProperties pClientProperties ) {
        // Hide if disabled, as otherwise users will see a generic Edit
        // Properties menu item, which can confuse even if disabled, as
        // generally this action will be modified for specific domain objects.
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "edit",
                                         "properties",
                                         "/icons/oxygenIcons/Edit16.png",
                                         true );
    }

    @SuppressWarnings("nls")
    public static XAction getEditRedoAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "edit",
                                         "redo",
                                         "/icons/deviantArt/EditRedo16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getEditReselectAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties, BUNDLE_NAME, "edit", "reselect", null );
    }

    @SuppressWarnings("nls")
    public static XAction getEditUndoAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "edit",
                                         "undo",
                                         "/icons/deviantArt/EditUndo16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorBackgroundGrayChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "backgroundGray",
                                         "/icons/mhschmieder/BackgroundGray16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorBlackChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "black",
                                         "/icons/mhschmieder/Black16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorBlueGrayChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "blueGray",
                                         "/icons/mhschmieder/BlueGray16.png" );
    }

    @SuppressWarnings("nls")
    public static XActionGroup getBackgroundColorChoiceGroup( final ClientProperties pClientProperties,
                                                              final BackgroundColorChoices backgroundColorChoices ) {
        final Collection< Action > backgroundColorChoiceCollection = backgroundColorChoices
                .getBackgroundColorChoiceCollection();

        final XActionGroup backgroundColorChoiceGroup = ActionFactory
                .makeChoiceGroup( pClientProperties,
                                  backgroundColorChoiceCollection,
                                  BUNDLE_NAME,
                                  "backgroundColor",
                                  "/icons/nineteenEightySeven/Colour16.png" );

        return backgroundColorChoiceGroup;
    }

    @SuppressWarnings("nls") 
    public static XAction getBackgroundColorDarkBlueGrayChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "darkBlueGray",
                                         "/icons/mhschmieder/DarkBlueGray16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorDarkCharcoalChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "darkCharcoal",
                                         "/icons/mhschmieder/DarkCharcoal16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorDarkGrayChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "darkGray",
                                         "/icons/mhschmieder/DarkGray16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorDarkSlateGrayChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "darkSlateGray",
                                         "/icons/mhschmieder/DarkSlateGray16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorDavysGrayChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "davysGray",
                                         "/icons/mhschmieder/DavysGray16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorDayChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "day",
                                         "/icons/mhschmieder/Day16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorDimGrayChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "dimGray",
                                         "/icons/mhschmieder/DimGray16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorGainsboroChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "gainsboro",
                                         "/icons/mhschmieder/Gainsboro16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorGrayChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "gray",
                                         "/icons/mhschmieder/Gray16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorGray25Choice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "gray25",
                                         "/icons/mhschmieder/Gray25%16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorLightBlueGrayChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "lightBlueGray",
                                         "/icons/mhschmieder/LightBlueGray16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorLightGrayChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "lightGray",
                                         "/icons/mhschmieder/LightGray16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorLightSlateGrayChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "lightSlateGray",
                                         "/icons/mhschmieder/lightSlateGray16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorMediumGrayChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "mediumGray",
                                         "/icons/mhschmieder/MediumGray16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorNightChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "night",
                                         "/icons/mhschmieder/Night16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorSlateGrayChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "slateGray",
                                         "/icons/mhschmieder/SlateGray16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorSpanishGrayChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "spanishGray",
                                         "/icons/mhschmieder/SpanishGray16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorWhiteChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "white",
                                         "/icons/mhschmieder/White16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getBackgroundColorWhiteSmokeChoice( final ClientProperties pClientProperties ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "backgroundColor",
                                         "whiteSmoke",
                                         "/icons/mhschmieder/WhiteSmoke16.png" );
    }
    
    @SuppressWarnings("nls")
    public static XAction getCloseWindowAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "file",
                                         "closeWindow",
                                         "/icons/happyIconStudio/CloseWindowBlack16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getCsvViewerAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "tools",
                                         "csvViewer",
                                         "/icons/led24/DocExcelCsv16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getExitAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "file",
                                         "exit",
                                         "/icons/damieng/StopRed16.png" );
    }

    @SuppressWarnings("nls")
    public static XActionGroup getExportActionGroup( final ClientProperties pClientProperties,
                                                     final ExportActions exportActions,
                                                     final boolean vectorGraphicsSupported,
                                                     final boolean renderedGraphicsSupported ) {
        final Collection< Action > exportActionCollection = exportActions
                .getExportActionCollection( vectorGraphicsSupported,
                                            renderedGraphicsSupported );

        final XActionGroup exportActionGroup = ActionFactory
                .makeActionGroup( pClientProperties,
                                  exportActionCollection,
                                  BUNDLE_NAME,
                                  "export",
                                  "/icons/happyIconStudio/ExportBlack16.png" );

        return exportActionGroup;
    }

    @SuppressWarnings("nls")
    public static XAction getExportImageGraphicsAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "export",
                                         "imageGraphics",
                                         "icons/fatCow/FileExtensionJpg16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getExportRenderedGraphicsAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "export",
                                         "renderedGraphics",
                                         "/icons/fatCow/FileExtensionEps16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getExportVectorGraphicsAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "export",
                                         "vectorGraphics",
                                         "icons/fatCow/FileExtensionPdf16.png" );
    }

    @SuppressWarnings("nls")
    public static XActionGroup getFileActionGroup( final ClientProperties pClientProperties,
                                                   final FileActions fileActions,
                                                   final boolean vectorGraphicsSupported,
                                                   final boolean renderedGraphicsSupported ) {
        final Collection< Action > fileActionCollection = fileActions
                .getFileActionCollection( pClientProperties,
                                          vectorGraphicsSupported,
                                          renderedGraphicsSupported );

        // TODO: Review whether this is the correct bundle name to use for this action group.
        final XActionGroup fileActionGroup = ActionFactory
                .makeActionGroup( pClientProperties,
                                  fileActionCollection,
                                  LabeledControlFactory.BUNDLE_NAME,
                                  "file",
                                  null );

        return fileActionGroup;
    }

    @SuppressWarnings("nls")
    public static XAction getFileNewProjectAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "file",
                                         "newProject",
                                         "/icons/oxygenIcons/FileNew16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getFileOpenProjectAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "file",
                                         "openProject",
                                         "/icons/led24/PageWhiteZip16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getFileProjectPropertiesAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "file",
                                         "projectProperties",
                                         "/icons/everaldo/PackageEditors16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getFileSaveProjectAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "file",
                                         "saveProject",
                                         "/icons/everaldo/FileSave16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getFileSaveProjectAsAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "file",
                                         "saveProjectAs",
                                         "/icons/everaldo/FileSaveAs16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getHelpAboutAction( final ClientProperties pClientProperties,
                                              final String applicationName ) {
        final XAction helpAction = ActionFactory
                .makeAction( pClientProperties,
                             BUNDLE_NAME,
                             "help",
                             "about",
                             "/icons/icojam/BlueInformation16.png" );

        // Modify the Menu Label to tag the Application Name.
        final String actionText = helpAction.getText();
        final String actionTextExtended = actionText + " " + applicationName;
        helpAction.setText( actionTextExtended );

        return helpAction;
    }

    @SuppressWarnings("nls")
    public static XAction getHelpAccountManagementAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "help",
                                         "accountManagement",
                                         "/icons/pc/MyAccount16.png" );
    }

    @SuppressWarnings("nls")
    public static XActionGroup getHelpActionGroup( final ClientProperties pClientProperties,
                                                   final HelpActions helpActions ) {
        final Collection< Action > helpActionCollection = helpActions.getHelpActionCollection();

        final XActionGroup helpActionGroup = ActionFactory
                .makeActionGroup( pClientProperties, helpActionCollection, BUNDLE_NAME, "help", null );

        return helpActionGroup;
    }

    @SuppressWarnings("nls")
    public static XAction getHelpCheckForUpdatesAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "help",
                                         "checkForUpdates",
                                         "/icons/ahaSoft/Update16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getHelpEulaAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "help",
                                         "eula",
                                         "/icons/fatCow/LicenseManagement16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getHelpHelpAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "help",
                                         "help",
                                         "/icons/icojam/BlueQuestion16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getHelpReleaseNotesAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "help",
                                         "releaseNotes",
                                         "/icons/fatCow/DocumentNotes16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getHelpSessionLogAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "help",
                                         "sessionLog",
                                         "/icons/everaldo/EasyMobLog16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getHelpTeamMembersAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "help",
                                         "teamMembers",
                                         "/icons/ahaSoft/People16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getHelpThirdPartyLibrariesAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "help",
                                         "thirdPartyLibraries",
                                         "/icons/pc/Library16.png" );
    }

    @SuppressWarnings("nls")
    public static XActionGroup getImportActionGroup( final ClientProperties clientProperties,
                                                     final ImportActions importActions,
                                                     final boolean imageGraphicsSupported,
                                                     final boolean vectorGraphicsSupported,
                                                     final boolean cadGraphicsSupported ) {
        final Collection< Action > importActionCollection = importActions
                .getImportActionCollection( imageGraphicsSupported,
                                            vectorGraphicsSupported,
                                            cadGraphicsSupported );

        final XActionGroup importActionGroup = ActionFactory
                .makeActionGroup( clientProperties,
                                  importActionCollection,
                                  LabeledActionFactory.BUNDLE_NAME,
                                  "import",
                                  "/icons/happyIconStudio/ImportBlack16.png" );

        return importActionGroup;
    }

    @SuppressWarnings("nls")
    public static XAction getImportCadGraphicsAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "import",
                                         "cadGraphics",
                                         "/icons/fatCow/FileExtensionDwg16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getImportImageGraphicsAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "import",
                                         "imageGraphics",
                                         "icons/fatCow/FileExtensionJpg16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getImportVectorGraphicsAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "import",
                                         "vectorGraphics",
                                         "icons/fatCow/FileExtensionPdf16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getLayerManagementAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "settings",
                                         "layerManagement",
                                         "/icons/ahaSoft/Layers16.png" );
    }

    @SuppressWarnings("nls")
    public static XActionGroup getLoadActionGroup( final ClientProperties clientProperties,
                                                   final LoadActions loadActions ) {
        final Collection< Action > loadActionCollection = loadActions
                .getLoadActionCollection();

        final XActionGroup loadActionGroup = ActionFactory
                .makeActionGroup( clientProperties,
                                  loadActionCollection,
                                  LabeledActionFactory.BUNDLE_NAME,
                                  "load",
                                  "/icons/everaldo/FileImport16.png" );

        return loadActionGroup;
    }

    @SuppressWarnings("nls")
    public static XAction getLoadProjectSettingsAction( final ClientProperties clientProperties ) {
        return ActionFactory.makeAction( clientProperties,
                                         BUNDLE_NAME,
                                         "load",
                                         "projectSettings",
                                         "/icons/fatCow/Cog16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getMeasurementUnitsAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "settings",
                                         "measurementUnits",
                                         "/icons/led24/RulerCorner16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getMouseToolChoice( final ClientProperties pClientProperties,
                                              final String itemName,
                                              final String jarRelativeIconFilename ) {
        return ActionFactory.makeChoice( pClientProperties,
                                         BUNDLE_NAME,
                                         "mouseTool",
                                         itemName,
                                         jarRelativeIconFilename );
    }

    @SuppressWarnings("nls")
    public static XAction getSelectToolChoice( final ClientProperties pClientProperties ) {
        return getMouseToolChoice( pClientProperties,
                                   "selectTool",
                                   "/icons/everaldo/Select16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getRotateToolChoice( final ClientProperties pClientProperties ) {
        return getMouseToolChoice( pClientProperties,
                                   "rotateTool",
                                   "/icons/everaldo/RotateCWLight16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getZoomToolChoice( final ClientProperties pClientProperties ) {
        return getMouseToolChoice( pClientProperties,
                                   "zoomTool",
                                   "/icons/everaldo/ViewMag16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getLineToolChoice( final ClientProperties pClientProperties ) {
        return getMouseToolChoice( pClientProperties,
                                   "lineTool",
                                   "/icons/yusukeKamiyamane/LayerShapeLine16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getPageSetupAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "file",
                                         "pageSetup",
                                         "/icons/yusukeKamiyamane/Setup16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getPrintAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "file",
                                         "print",
                                         "/icons/everaldo/FilePrint16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getProjectReportAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "tools",
                                         "projectReport",
                                         "/icons/ahaSoft/Report16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getSelectAllAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "select",
                                         "all",
                                         "/icons/deviantArt/EditSelectAll16.png" );
    }

    @SuppressWarnings("nls")
    public static XActionGroup getSettingsActionGroup( final ClientProperties pClientProperties,
                                                       final SettingsActions settingsActions,
                                                       final boolean maximumSizeSupported ) {
        final Collection< Action > settingsActionCollection = settingsActions
                .getSettingsActionCollection( pClientProperties, maximumSizeSupported );

        final XActionGroup settingsActionGroup = ActionFactory
                .makeActionGroup( pClientProperties,
                                  settingsActionCollection,
                                  BUNDLE_NAME,
                                  "settings",
                                  null );

        return settingsActionGroup;
    }

    @SuppressWarnings("nls")
    public static XAction getSettingsGesturesCheck( final ClientProperties pClientProperties ) {
        return ActionFactory.makeCheck( pClientProperties, BUNDLE_NAME, "settings", "gestures", null );
    }

    @SuppressWarnings("nls")
    public static XAction getTestClearPreferencesAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "test",
                                         "clearPreferences",
                                         "/icons/oxygenIcons/EditClearList16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getTestGraphicsImportLoggingCheck( final ClientProperties pClientProperties ) {
        return ActionFactory.makeCheck( pClientProperties,
                                        BUNDLE_NAME,
                                        "test",
                                        "graphicsImportLogging",
                                        "/icons/fatCow/FileExtensionLog16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getTestSaveServerRequestAction( final ClientProperties pClientProperties ) {
        return ActionFactory
                .makeAction( pClientProperties, BUNDLE_NAME, "test", "saveServerRequest", null );
    }

    @SuppressWarnings("nls")
    public static XAction getTestSaveServerResponseAction( final ClientProperties pClientProperties ) {
        return ActionFactory
                .makeAction( pClientProperties, BUNDLE_NAME, "test", "saveServerResponse", null );
    }

    @SuppressWarnings("nls")
    public static XAction getTestSvgViewerMenuItem( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "test",
                                         "svgViewer",
                                         "/icons/oxygenIcons/SvgMimeType16.png" );
    }

    @SuppressWarnings("nls")
    public static XActionGroup getToolsActionGroup( final ClientProperties pClientProperties,
                                                    final ToolsActions toolsActions ) {
        final Collection< Action > toolsActionCollection = toolsActions
                .getToolsActionCollection( pClientProperties );

        final XActionGroup toolsActionGroup = ActionFactory.makeActionGroup( pClientProperties,
                                                                             toolsActionCollection,
                                                                             BUNDLE_NAME,
                                                                             "tools",
                                                                             null );

        return toolsActionGroup;
    }

    @SuppressWarnings("nls")
    public static XAction getViewImportedGraphicsOpacityAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "view",
                                         "importedGraphicsOpacity",
                                         "/icons/ahaSoft/TransparentColor16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getViewRefreshAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "view",
                                         "refresh",
                                         "/icons/deviantArt/PowerRestartInvert16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getViewResetAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "view",
                                         "reset",
                                         "/icons/nineteenEightySeven/FormReset16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getViewShowAxisZeroLinesCheck( final ClientProperties pClientProperties ) {
        return ActionFactory.makeCheck( pClientProperties,
                                        BUNDLE_NAME,
                                        "view",
                                        "showAxisZeroLines",
                                        "/icons/yusukeKamiyamane/BorderInside16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getViewShowCursorCoordinatesCheck( final ClientProperties pClientProperties ) {
        return ActionFactory.makeCheck( pClientProperties,
                                        BUNDLE_NAME,
                                        "view",
                                        "showCursorCoordinates",
                                        "/icons/damieng/MapCursor16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getViewShowImportedGraphicsCheck( final ClientProperties pClientProperties ) {
        return ActionFactory.makeCheck( pClientProperties,
                                        BUNDLE_NAME,
                                        "view",
                                        "showImportedGraphics",
                                        "/icons/deviantArt/Blueprint16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getViewZoomInAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "view",
                                         "zoomIn",
                                         "/icons/everaldo/ons/ViewMagPlus16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction getViewZoomOutAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "view",
                                         "zoomOut",
                                         "/icons/everaldo/ViewMagMinus16.png" );
    }
}
