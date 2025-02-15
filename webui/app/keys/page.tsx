"use client";
import ApiKeyLayout from "@/layout/ApiKeyLayout";
import { Key, useCallback, useEffect, useMemo, useState } from "react";
import {
  Button,
  Chip,
  Selection,
  Dropdown,
  DropdownItem,
  DropdownMenu,
  DropdownTrigger,
  SortDescriptor,
  Table,
  TableBody,
  TableCell,
  TableColumn,
  TableHeader,
  TableRow,
  Pagination,
  Input,
  Modal,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalFooter,
} from "@heroui/react";
import {
  ChevronDownIcon,
  PlusIcon,
  SearchIcon,
  VerticalDotsIcon,
} from "@/components/icons";
import { useRouter } from "next/navigation";
import NMachineNewDrawer from "@/components/MachineNewDrawer";
import { MachineType } from "@/api/internal/model/response/machine";
import { api } from "@/api/instance";
import { PopMsg } from "@/store/pops";
import { CopyToClipBoard } from "@/components/CopyedText";
import MachineEditorDrawer from "@/components/MachineEditor";

export default function ApiKeysPage() {
  const router = useRouter();
  const [isOpenNewMachineDrawer, setIsOpenNewMachineDrawer] = useState(false);
  const [machineList, setMachineList] = useState<Array<MachineType>>([]);
  const [sortDescriptor, setSortDescriptor] = useState<SortDescriptor>({
    column: "id",
    direction: "ascending",
  });
  const [searchFilterValue, setSearchFilterValue] = useState("");
  const [activeFilter, setActiveFilter] = useState<Selection>("all");
  const hasSearchFilter = Boolean(searchFilterValue);
  const [curPage, setCurPage] = useState(1);
  const [rowPerPage, setRowPerPage] = useState(10);
  const [visibleColumns, setVisibleColumns] = useState<Selection>(
    new Set(INITIAL_VISIBLE_COLUMNS)
  );
  const [curDeleteMachine, setCurDeleteMachine] = useState<{
    id: string;
    name: string;
  } | null>(null);

  const [curEditorMachine, setCurEditorMachine] = useState<MachineType | null>(
    null
  );
  const onRowsPerPageChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setRowPerPage(Number(e.target.value));
    setCurPage(1);
  };

  const MachineListItemfiltered = useMemo(() => {
    let filtered = [...machineList];

    if (hasSearchFilter) {
      filtered = filtered.filter((machine) =>
        machine.name.toLowerCase().includes(searchFilterValue.toLowerCase())
      );
    }
    if (
      activeFilter !== "all" &&
      Array.from(activeFilter).length !== activeOptions.length
    ) {
      filtered = filtered.filter((machine) =>
        Array.from(activeFilter).includes(machine.active)
      );
    }

    return filtered;
  }, [machineList, searchFilterValue, activeFilter]);

  const pageCount = useMemo(() => {
    return Math.ceil(MachineListItemfiltered.length / rowPerPage);
  }, [MachineListItemfiltered, rowPerPage]);

  const machineListItem = useMemo<Array<MachineType>>(() => {
    const start = (curPage - 1) * rowPerPage;
    const end = start + rowPerPage;

    return MachineListItemfiltered.slice(start, end);
  }, [MachineListItemfiltered, curPage, rowPerPage]);

  const machineListItemSorted = useMemo<Array<MachineType>>(() => {
    return [...machineListItem].sort((a, b) => {
      const first = a[sortDescriptor.column as keyof MachineType] as string;
      const second = b[sortDescriptor.column as keyof MachineType] as string;
      const cmp = first < second ? -1 : first > second ? 1 : 0;

      return sortDescriptor.direction === "descending" ? -cmp : cmp;
    });
  }, [sortDescriptor, machineListItem]);

  const fetchMachineList = async () => {
    const res = await api.machineService.list();
    if (res.code !== 200) {
      PopMsg({
        type: "danger",
        title: "获取机器列表失败",
        description: res.message,
      });
    }
    setMachineList(res.data);
  };

  const deleteMachine = async () => {
    if (!curDeleteMachine) return;

    const res = await api.machineService.delete({
      id: curDeleteMachine.id,
    });

    if (res.code != 200) {
      PopMsg({
        type: "danger",
        title: `删除${curDeleteMachine.name}失败`,
        description: res.message,
      });
    }

    setCurDeleteMachine(null);
    fetchMachineList();

    PopMsg({
      type: "success",
      title: `删除${curDeleteMachine.name}成功`,
    });
  };

  useEffect(() => {
    fetchMachineList();
  }, []);

  const renderCell = useCallback((machine: MachineType, columnKey: Key) => {
    const cellValue = machine[columnKey as keyof MachineType];

    switch (columnKey) {
      case "token":
        return (
          <button onClick={() => CopyToClipBoard(machine.token)}>
            {machine.token}
          </button>
        );
      case "active":
        return (
          <Chip
            className="capitalize"
            color={cellValue === "yes" ? "success" : "danger"}
            size="sm"
            variant="flat"
          >
            {cellValue === "yes" ? "已激活" : "未激活"}
          </Chip>
        );

      case "actions":
        return (
          <div className="relative flex justify-end items-center gap-2">
            <Dropdown>
              <DropdownTrigger>
                <Button isIconOnly size="sm" variant="light">
                  <VerticalDotsIcon className="text-default-300" />
                </Button>
              </DropdownTrigger>
              <DropdownMenu>
                <DropdownItem
                  key="view"
                  onPress={() => {
                    router.push(`/machine/${machine.id}`);
                  }}
                >
                  查看
                </DropdownItem>
                <DropdownItem
                  key="edit"
                  onPress={() => {
                    setCurEditorMachine(machine);
                  }}
                >
                  编辑
                </DropdownItem>
                <DropdownItem
                  key="delete"
                  onPress={() => {
                    setCurDeleteMachine({
                      id: machine.id,
                      name: machine.name,
                    });
                  }}
                >
                  删除
                </DropdownItem>
              </DropdownMenu>
            </Dropdown>
          </div>
        );

      default:
        return cellValue;
    }
  }, []);

  const onSearchChange = (value?: string) => {
    if (value) {
      setSearchFilterValue(value);
      setCurPage(1);
    } else {
      setSearchFilterValue("");
    }
  };
  const onClear = () => {
    setSearchFilterValue("");
    setCurPage(1);
  };

  const topContent = useMemo(() => {
    return (
      <div className="flex flex-col gap-4">
        <div className="flex justify-between gap-3 items-end">
          <Input
            isClearable
            className="w-full sm:max-w-[44%]"
            placeholder="搜索"
            startContent={<SearchIcon />}
            value={searchFilterValue}
            onClear={() => onClear()}
            onValueChange={onSearchChange}
          />
          <div className="flex gap-3">
            <Dropdown>
              <DropdownTrigger className="hidden sm:flex">
                <Button
                  endContent={<ChevronDownIcon className="text-small" />}
                  variant="flat"
                >
                  状态
                </Button>
              </DropdownTrigger>
              <DropdownMenu
                disallowEmptySelection
                aria-label="Table Columns"
                closeOnSelect={false}
                selectedKeys={activeFilter}
                selectionMode="multiple"
                onSelectionChange={setActiveFilter}
              >
                {activeOptions.map((active) => (
                  <DropdownItem key={active.id} className="capitalize">
                    {active.label}
                  </DropdownItem>
                ))}
              </DropdownMenu>
            </Dropdown>
            <Dropdown>
              <DropdownTrigger className="hidden sm:flex">
                <Button
                  endContent={<ChevronDownIcon className="text-small" />}
                  variant="flat"
                >
                  显示行
                </Button>
              </DropdownTrigger>
              <DropdownMenu
                disallowEmptySelection
                aria-label="Table Columns"
                closeOnSelect={false}
                selectedKeys={visibleColumns}
                selectionMode="multiple"
                onSelectionChange={setVisibleColumns}
              >
                {columns.map((column) => (
                  <DropdownItem key={column.id} className="capitalize">
                    {column.label}
                  </DropdownItem>
                ))}
              </DropdownMenu>
            </Dropdown>
            <Button
              color="primary"
              endContent={<PlusIcon />}
              onPress={() => setIsOpenNewMachineDrawer(true)}
            >
              新建主机
            </Button>
          </div>
        </div>
        <div className="flex justify-between items-center">
          <div className="flex items-center gap-2">
            <span className="text-default-400 text-small">
              总共 {machineList.length} 台主机
            </span>
          </div>
          <label className="flex items-center text-default-400 text-small gap-1">
            每页行数:
            <select
              className="bg-transparent outline-none text-default-400 text-small"
              onChange={onRowsPerPageChange}
            >
              <option value="5">5</option>
              <option value="10">10</option>
              <option value="15">15</option>
            </select>
          </label>
        </div>
      </div>
    );
  }, [
    searchFilterValue,
    activeFilter,
    visibleColumns,
    onSearchChange,
    onRowsPerPageChange,
    machineList.length,
    hasSearchFilter,
  ]);

  const bottomContent = useMemo(() => {
    return (
      <div className="py-2 px-2 flex justify-center items-center">
        <Pagination
          isCompact
          showControls
          showShadow
          color="primary"
          page={curPage}
          total={pageCount}
          onChange={setCurPage}
        />
      </div>
    );
  }, [machineListItem.length, curPage, pageCount, hasSearchFilter]);

  return (
    <ApiKeyLayout>
      <div className="flex flex-col">
        <Table
          aria-label="Machine Table"
          classNames={{
            wrapper: "max-h-[100%]",
          }}
          onSortChange={setSortDescriptor}
          sortDescriptor={{
            column: sortDescriptor.column!,
            direction: sortDescriptor.direction!,
          }}
          topContent={topContent}
          topContentPlacement="outside"
          bottomContent={bottomContent}
          bottomContentPlacement="outside"
        >
          <TableHeader columns={columns}>
            {(column) => (
              <TableColumn key={column.id} allowsSorting={column.sortable}>
                {column.label}
              </TableColumn>
            )}
          </TableHeader>
          <TableBody items={machineListItemSorted} emptyContent="无数据">
            {(item) => (
              <TableRow key={item.id}>
                {(columnKey) => (
                  <TableCell>{renderCell(item, columnKey)}</TableCell>
                )}
              </TableRow>
            )}
          </TableBody>
        </Table>
        <NMachineNewDrawer
          isOpen={isOpenNewMachineDrawer}
          openChange={(value) => setIsOpenNewMachineDrawer(value)}
          callBack={() => {
            fetchMachineList();
          }}
        />
        <Modal
          isOpen={curDeleteMachine != null}
          onClose={() => {
            setCurDeleteMachine(null);
          }}
        >
          <ModalContent>
            {(onClose) => (
              <>
                <ModalHeader className="">
                  <span>确定删除</span>
                  <Chip className="mx-1">{curDeleteMachine?.name}</Chip>
                  <span>吗？</span>
                </ModalHeader>
                <ModalBody>
                  <p>
                    你应该清楚自己在删除此主机，否则可能会造成不可预知的后果。
                  </p>
                </ModalBody>
                <ModalFooter>
                  <Button color="danger" variant="light" onPress={onClose}>
                    取消
                  </Button>
                  <Button color="primary" onPress={() => deleteMachine()}>
                    删除
                  </Button>
                </ModalFooter>
              </>
            )}
          </ModalContent>
        </Modal>
        {curEditorMachine != null && (
          <MachineEditorDrawer
            machine={curEditorMachine}
            closeOpen={() => setCurEditorMachine(null)}
            callBack={fetchMachineList}
          />
        )}
      </div>
    </ApiKeyLayout>
  );
}

const columns = [
  { label: "ID", id: "id", sortable: true },
  { label: "主机名称", id: "name", sortable: true },
  { label: "地区", id: "location", sortable: true },
  { label: "节点名称", id: "nodeName", sortable: true },
  { label: "API Key", id: "token" },
  { label: "创建日期", id: "registerTime", sortable: true },
  { label: "状态", id: "active", sortable: true },
  { label: "操作", id: "actions" },
];
const INITIAL_VISIBLE_COLUMNS = [
  "id",
  "name",
  "location",
  "nodeName",
  "token",
  "registerTime",
  "active",
  "actions",
];

const activeOptions = [
  { label: "已激活", id: "yes" },
  { label: "未激活", id: "no" },
];
