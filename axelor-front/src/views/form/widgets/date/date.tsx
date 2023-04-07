import moment from "dayjs";
import { Box, useClassNames } from "@axelor/ui";
import { useAtom } from "jotai";
import {
  FocusEvent,
  KeyboardEvent,
  MouseEvent,
  SyntheticEvent,
  useCallback,
  useMemo,
  useRef,
  useState,
} from "react";
import { MaterialIcon } from "@axelor/ui/icons/meterial-icon";

import { Field } from "@/services/client/meta.types";
import { FieldContainer, FieldProps } from "../../builder";
import { Picker } from "./picker";
import { i18n } from "@/services/client/i18n";
import { DateInput } from "./date-input";
import { TimeInput } from "./time-input";
import { l10n } from "@/services/client/l10n";
import { getDateTimeFormat, getTimeFormat } from "@/utils/format";
import customParseFormat from "dayjs/plugin/customParseFormat";
moment.extend(customParseFormat);

function focusInput(inputEl?: HTMLElement) {
  let input = inputEl && inputEl.querySelector("input,textarea");
  if (!input && ["INPUT", "TEXTAREA"].includes(inputEl?.tagName!)) {
    input = inputEl;
  }
  input && (input as HTMLInputElement).focus();
  input && (input as HTMLInputElement).select();
}

function toCalendarFormat(format: string) {
  return format
    .split("")
    .map((c) => (["M", "H"].includes(c) ? c : `${c}`.toLowerCase()))
    .join("");
}

export function Date({ schema, readonly, valueAtom }: FieldProps<string>) {
  const { uid, title } = schema;
  const pickerRef = useRef<any>();
  const boxRef = useRef<HTMLDivElement>(null);
  const classNames = useClassNames();
  const [open, setOpen] = useState(false);
  const [changed, setChanged] = useState(false);
  const [value, setValue] = useAtom(valueAtom);

  const type = (schema.widget || schema.serverType || schema.type)!;
  const dateFormats = useMemo<Record<string, string[]>>(
    () => ({
      datetime: [
        "YYYY-MM-DDTHH:mm",
        getDateTimeFormat({ props: schema as Field }),
      ],
      date: ["YYYY-MM-DD", l10n.getDateFormat()],
      time: ["HH:mm", getTimeFormat({ props: schema as Field })],
    }),
    [schema]
  );
  const [valueFormat, format] =
    dateFormats[type?.toLowerCase()] || dateFormats.date;

  const getInput = useCallback(() => {
    const calendar = pickerRef.current;
    return calendar?.input?.inputElement as HTMLElement;
  }, []);

  const handleOpen = useCallback((focus?: boolean) => {
    setOpen(true);
    if (focus) {
      setTimeout(() => {
        const calendar = pickerRef.current;
        const selectedDay = calendar?.calendar?.componentNode.querySelector(
          '.react-datepicker__day[tabindex="0"]'
        );
        selectedDay && selectedDay.focus({ preventScroll: true });
      }, 100);
    }
  }, []);

  const handleClose = useCallback(
    (focus?: boolean) => {
      setOpen(false);
      focus === true && focusInput(getInput());
    },
    [getInput]
  );

  const handleBlur = useCallback(
    (e: FocusEvent<HTMLInputElement>) => {
      if (changed) {
        const value = e?.target?.value || null;
        setValue(
          value && moment(value).isValid()
            ? moment(value, format).format(valueFormat)
            : null,
          true
        );
        setChanged(false);
      }
    },
    [changed, format, valueFormat, setValue]
  );

  const handleClickOutSide = useCallback(
    (event: MouseEvent<HTMLDivElement>) => {
      const container = boxRef.current;
      if (container && container.contains(event.target as Node)) {
        return;
      }
      handleClose();
    },
    [handleClose]
  );

  const handleKeyDown = useCallback(
    (e: KeyboardEvent<HTMLElement>) => {
      if (open) {
        if (["Tab", "Escape"].includes(e.key)) {
          handleClose(true);
        }
        e.preventDefault();
      }
    },
    [open, handleClose]
  );

  const handleSelect = useCallback(() => {
    handleClose(true);
  }, [handleClose]);

  const handleChange = useCallback(
    (value: Date | null, event: SyntheticEvent) => {
      const callOnChange = event.type === "click" ? true : false;
      setValue(
        value && moment(value, format).isValid()
          ? moment(value).format(valueFormat)
          : null,
        callOnChange
      );
      setChanged(!callOnChange);
    },
    [valueFormat, setValue]
  );

  const momentValue = value ? moment(value, format) : null;
  return (
    <FieldContainer readonly={readonly}>
      <label htmlFor={uid}>{title}</label>
      {readonly ? (
        momentValue?.isValid() && momentValue.format(format)
      ) : (
        <Box ref={boxRef} d="flex">
          <Picker
            showMonthDropdown
            showYearDropdown
            todayButton={i18n.get("Today")}
            className={classNames("form-control")}
            showPopperArrow={false}
            portalId="root-app"
            autoFocus={open}
            open={open}
            ref={pickerRef}
            dateFormat={toCalendarFormat(format)}
            selected={momentValue?.toDate ? momentValue.toDate() : null}
            customInput={
              <DateInput
                eventOnBlur={handleBlur}
                format={format}
                open={open}
                onOpen={handleOpen}
                onClose={handleClose}
              />
            }
            timeInputLabel={
              (
                <Box>
                  <MaterialIcon icon="schedule" weight={300} opticalSize={20} />
                </Box>
              ) as any
            }
            showTimeInput={type?.toLowerCase() !== "date"}
            customTimeInput={
              <TimeInput format={format} onClose={handleClose} />
            }
            onSelect={handleSelect}
            onChange={handleChange}
            onBlur={handleBlur}
            onKeyDown={handleKeyDown}
            onClickOutside={handleClickOutSide}
          />
        </Box>
      )}
    </FieldContainer>
  );
}
