'use client';

import { ColumnDef, flexRender, getCoreRowModel, getSortedRowModel, useReactTable, SortingState } from '@tanstack/react-table';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { DataTablePagination } from '@/components/DataTablePagination';
import { useEffect, useState } from 'react';
import { Pagination } from '@/types';
import { GET } from '@/lib/fetch';
import { TableData } from '@/components/table/Columns';

interface DataTableProps<TValue> {
  columns: ColumnDef<TableData, TValue>[];
}

export function DataTable<TValue>({ columns }: DataTableProps<TValue>) {
  const [tableData, setTableData] = useState<TableData[]>([]);
  const [paginationData, setPaginationData] = useState<Pagination>();
  const [pagination, setPagination] = useState({
    pageIndex: 0,
    pageSize: 10,
  });
  const [sorting, setSorting] = useState<SortingState>([{ desc: true, id: 'id' }]);
  console.log(sorting);

  useEffect(() => {
    GET(
      `influencer?page=${pagination.pageIndex}&size=${pagination.pageSize}&direction=${sorting[0].desc ? 'DESC' : 'ASC'}&sortBy=${sorting[0].id}`,
      'no-cache',
    ).then((pagination: Pagination) => {
      const table: TableData[] = pagination.content.map(({ id, twitter }) => ({
        id: id,
        twitterId: twitter.id,
        twitterUsername: twitter.username,
        isAuthorized: twitter.auth.isAuthorized,
      }));

      setPaginationData(pagination);
      setTableData(table);
    });
  }, [pagination, sorting]);

  const table = useReactTable({
    data: tableData,
    columns,
    getCoreRowModel: getCoreRowModel(),
    manualPagination: true,
    pageCount: paginationData?.totalPages,
    rowCount: paginationData?.size,
    state: { pagination, sorting },
    onPaginationChange: setPagination,
    onSortingChange: setSorting,
  });

  return (
    <>
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => {
                  return (
                    <TableHead key={header.id}>
                      {header.isPlaceholder ? null : flexRender(header.column.columnDef.header, header.getContext())}
                    </TableHead>
                  );
                })}
              </TableRow>
            ))}
          </TableHeader>
          <TableBody>
            {table.getRowModel().rows?.length ? (
              table.getRowModel().rows.map((row) => (
                <TableRow key={row.id} data-state={row.getIsSelected() && 'selected'}>
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id}>{flexRender(cell.column.columnDef.cell, cell.getContext())}</TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={columns.length} className="h-24 text-center">
                  No results.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
      <DataTablePagination table={table} />
    </>
  );
}
