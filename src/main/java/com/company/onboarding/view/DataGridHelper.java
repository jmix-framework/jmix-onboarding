package com.company.onboarding.view;

import com.company.onboarding.entity.User;
import com.vaadin.flow.component.grid.Grid;
import io.jmix.flowui.component.grid.DataGrid;

import java.util.ArrayList;
import java.util.List;

public class DataGridHelper {

    public static <E> void setDataGridColumnPosition(DataGrid<E> dataGrid, Grid.Column<E> column, int position) {
        List<Grid.Column<E>> columns = new ArrayList<>(dataGrid.getColumns());
        columns.remove(column);
        columns.add(position, column);
        dataGrid.setColumnOrder(columns);
    }
}
