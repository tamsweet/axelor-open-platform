import { useAtomValue } from "jotai";
import { uniqueId } from "lodash";
import { useCallback } from "react";

import { Box, Button } from "@axelor/ui";

import { dialogs } from "@/components/dialogs";
import { DataContext, DataRecord } from "@/services/client/data.types";
import { i18n } from "@/services/client/i18n";
import { showPopup } from "@/view-containers/view-popup";
import { usePopupHandlerAtom } from "@/view-containers/view-popup/handler";

import { initTab } from "../use-tabs";

export type EditorOptions = {
  model: string;
  title: string;
  record?: DataRecord | null;
  readonly?: boolean;
  viewName?: string;
  context?: DataContext;
  onSave?: (record: DataRecord) => Promise<DataRecord> | void;
  onSelect?: (record: DataRecord) => void;
};

export function useEditor() {
  return useCallback(async (options: EditorOptions) => {
    const {
      title,
      model,
      record,
      viewName,
      context,
      readonly: forceReadonly,
      onSave,
      onSelect,
    } = options;
    const tab = await initTab({
      name: uniqueId("$selector"),
      title,
      model,
      viewType: "form",
      views: [{ type: "form", name: viewName }],
      params: {
        popup: true,
        forceReadonly,
        "show-toolbar": false,
        "_popup-record": record,
      },
      context: {
        _showRecord: record?.id,
        ...context,
      },
    });

    if (!tab) return;

    const close = await showPopup({
      tab,
      open: true,
      onClose: () => {},
      footer: () => (
        <Footer onSave={onSave} onClose={() => close()} onSelect={onSelect} />
      ),
      buttons: [],
    });
  }, []);
}

function Footer({
  onClose,
  onSave,
  onSelect,
}: {
  onClose: () => void;
  onSave?: EditorOptions["onSave"];
  onSelect?: EditorOptions["onSelect"];
}) {
  const handlerAtom = usePopupHandlerAtom();
  const handler = useAtomValue(handlerAtom);

  const handleClose = useCallback(() => {
    dialogs.confirmDirty(
      async () => handler.getState?.().dirty ?? false,
      async () => onClose()
    );
  }, [handler, onClose]);

  const handleConfirm = useCallback(async () => {
    if (handler.getState === undefined) return onClose();
    const state = handler.getState();
    const record = state.record;
    const canSave = state.dirty || !record.id;
    try {
      if (canSave) {
        if (onSave) {
          onSave(record);
        } else if (onSelect && handler.onSave) {
          const rec = await handler.onSave();
          onSelect(rec);
        }
      }
      onClose();
    } catch (e) {
      // TODO: show error
    }
  }, [handler, onClose, onSave, onSelect]);

  return (
    <Box d="flex" g={2}>
      <Button variant="secondary" onClick={handleClose}>
        {i18n.get("Cancel")}
      </Button>
      <Button variant="primary" onClick={handleConfirm}>
        {i18n.get("OK")}
      </Button>
    </Box>
  );
}
