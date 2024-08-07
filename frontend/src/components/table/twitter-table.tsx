'use client';

import { ColumnDef, flexRender, getCoreRowModel, SortingState, useReactTable } from '@tanstack/react-table';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Pagination } from '@/components/table/pagination';
import { useEffect, useState } from 'react';
import { Pagination as PaginationType } from '@/types';
import { GET } from '@/lib/fetch';
import { TableData } from '@/components/table/columns';
import { Skeleton } from '@/components/ui/skeleton';

interface DataTableProps<TValue> {
  columns: ColumnDef<TableData, TValue>[];
}

export function TwitterTable<TValue>({ columns }: DataTableProps<TValue>) {
  const [tableData, setTableData] = useState<TableData[]>([]);
  const [paginationData, setPaginationData] = useState<PaginationType>();
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
    ).then((pagination: PaginationType) => {
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
    <div className="flex flex-col gap-4">
      {tableData.length ? (
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
          <Pagination table={table} />
        </>
      ) : (
        <>
          <Skeleton className="h-48" />
          <Skeleton className="h-8" />
        </>
      )}
    </div>
  );
}
