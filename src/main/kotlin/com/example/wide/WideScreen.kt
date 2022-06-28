package com.example.wide

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.vfs.VirtualFileManager
import javax.swing.JSplitPane

class WideScreen : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val manager = event.project?.let { FileEditorManager.getInstance(it) }
        val windowMan = event.project?.let { FileEditorManagerEx.getInstanceEx(it) }
        val currentWindow = windowMan?.currentWindow
        windowMan?.unsplitAllWindow()

        windowMan?.createSplitter(JSplitPane.HORIZONTAL_SPLIT, currentWindow)
        val leftWindow = windowMan?.windows?.get(0)
        val createSplitter = windowMan?.createSplitter(JSplitPane.HORIZONTAL_SPLIT, leftWindow)

        val selectedEditor = event.project?.let { FileEditorManager.getInstance(it).selectedTextEditor}
                selectedEditor?.scrollingModel?.addVisibleAreaListener(  {
                    System.out.println(FileDocumentManager.getInstance().getFile(selectedEditor.document)?.path)
                    val editors = EditorFactory.getInstance().getEditors(it.editor.document);
                    val filteredEditors = editors.filter { it != selectedEditor }
                    filteredEditors[0].scrollingModel.scrollVertically(it.newRectangle.y - it.newRectangle.height)
                    filteredEditors[1].scrollingModel.scrollVertically(it.newRectangle.y + it.newRectangle.height)

                    val totalFold = let { selectedEditor.foldingModel
                        .allFoldRegions
                        .filter { !it.isExpanded }
                        .sumOf { Math.abs((it.document.getLineNumber(it.endOffset) - it.document.getLineNumber(it.startOffset)) * it.editor.lineHeight)
                        }}

                    val totalHeight = (selectedEditor?.lineHeight * selectedEditor?.document.lineCount) - totalFold

                    if(selectedEditor?.scrollingModel.visibleArea.y < selectedEditor?.scrollingModel.visibleArea.height && !(selectedEditor?.scrollingModel.visibleArea.height > totalHeight))
                    {
                        selectedEditor.scrollingModel.scrollVertically(it.newRectangle.height)
                    }
                    else if(selectedEditor?.scrollingModel.visibleArea.y > totalHeight
                        - (2*selectedEditor.scrollingModel.visibleArea.height)
                        && !(selectedEditor?.scrollingModel.visibleArea.height > totalHeight))
                    {
                        selectedEditor.scrollingModel.scrollVertically(totalHeight - (2 * selectedEditor?.scrollingModel.visibleArea.height))
                    }
                })
            }
    }