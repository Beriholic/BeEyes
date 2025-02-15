import { Spinner } from "@heroui/react";
import { AnimatePresence, motion } from "framer-motion";

export default function Loading() {
  return (
    <AnimatePresence>
      <motion.div
        className="flex flex-col gap-2 justify-center fixed top-1/2 left-1/2"
        initial={{ opacity: 0, y: -50, scale: 0.3 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        exit={{ opacity: 0, y: 20, scale: 0.5 }}
      >
        <Spinner size="lg" />
        <div className="text-xl">加载中</div>
      </motion.div>
    </AnimatePresence>
  );
}
