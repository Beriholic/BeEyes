"use client";

import { usePopsStore } from "@/store/pops";
import { Alert } from "@heroui/react";
import { AnimatePresence, motion } from "framer-motion";

export function Pops() {
  const msgList = usePopsStore((state) => state.msgList);

  return (
    <div className="fixed top-0 right-0 z-[100] flex flex-col w-1/4 p-2 gap-2 overflow-hidden">
      <AnimatePresence>
        {msgList.map((msg) => (
          <motion.div
            key={msg.id}
            layout
            initial={{ opacity: 0, y: -50, scale: 0.3 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 20, scale: 0.5 }}
            transition={{ type: "spring", damping: 20, stiffness: 200 }}
          >
            <Alert
              color={msg.type}
              description={msg.description}
              title={msg.title}
            />
          </motion.div>
        ))}
      </AnimatePresence>
    </div>
  );
}
