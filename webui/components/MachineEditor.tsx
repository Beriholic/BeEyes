import {
  Button,
  Drawer,
  DrawerBody,
  DrawerContent,
  DrawerFooter,
  DrawerHeader,
  Input,
} from "@heroui/react";
import { FaServer, FaEarthAsia, FaCircleNodes } from "react-icons/fa6";
import { MachineType } from "@/api/internal/model/response/machine";
import { useState } from "react";
import { MachineServiceRequest } from "@/api/internal/model/request/machine";
import { api } from "@/api/instance";
import { PopMsg } from "@/store/pops";

interface MachineEditorProps {
  machine: MachineType;
  closeOpen: () => void;
  callBack?: () => void;
}
export default function MachineEditorDrawer({
  machine,
  closeOpen,
  callBack,
}: MachineEditorProps) {
  const [newMachine, setNewMachine] = useState<
    MachineServiceRequest["MACHINE_SERVICE/UPDATE"]
  >(() => {
    return {
      id: machine.id,
      name: machine.name,
      location: machine.location,
      nodeName: machine.nodeName,
    };
  });
  const setName = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNewMachine({
      ...newMachine,
      name: e.target.value,
    });
  };
  const setLocation = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNewMachine({
      ...newMachine,
      location: e.target.value,
    });
  };
  const setNodeName = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNewMachine({
      ...newMachine,
      nodeName: e.target.value,
    });
  };
  const onClose = () => {
    closeOpen();
    callBack?.();
  };

  const save = async () => {
    const res = await api.machineService.update({
      id: newMachine.id,
      name: newMachine.name,
      location: newMachine.location,
      nodeName: newMachine.nodeName,
    });
    if (res.code !== 200) {
      PopMsg({
        type: "danger",
        title: "保存失败",
        description: res.message,
      });
      return;
    }

    PopMsg({
      type: "success",
      title: "保存成功",
    });
    onClose();
  };

  return (
    <Drawer isOpen={machine != null} onOpenChange={onClose}>
      <DrawerContent>
        {(onClose) => (
          <>
            <DrawerHeader className="flex flex-col gap-1">
              编辑主机
            </DrawerHeader>
            <DrawerBody>
              <div className="flex items-center gap-2">
                <FaServer size={24} />
                <Input
                  label="主机名称"
                  value={newMachine.name}
                  onChange={setName}
                />
              </div>
              <div className="flex items-center gap-2">
                <FaEarthAsia size={24} />
                <Input
                  label="地区"
                  value={newMachine.location}
                  onChange={setLocation}
                />
              </div>
              <div className="flex items-center gap-2">
                <FaCircleNodes size={24} />
                <Input
                  label="节点名称"
                  value={newMachine.nodeName}
                  onChange={setNodeName}
                />
              </div>
            </DrawerBody>
            <DrawerFooter>
              <Button color="danger" variant="light" onPress={onClose}>
                关闭
              </Button>
              <Button color="primary" onPress={save}>
                保存修改
              </Button>
            </DrawerFooter>
          </>
        )}
      </DrawerContent>
    </Drawer>
  );
}
