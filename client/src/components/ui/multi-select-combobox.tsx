"use client";

import * as React from "react";
import { X, Check, ChevronsUpDown } from "lucide-react";

import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Badge } from "@/components/ui/badge";

export type ComboboxItem = {
  value: string;
  label: string;
  color?: string;
};

interface MultiSelectComboboxProps {
  items: ComboboxItem[];
  placeholder: string;
  searchPlaceholder: string;
  emptyMessage: string;
  selectedValues: string[];
  onValueChange: (values: string[]) => void;
  className?: string;
  disabled?: boolean;
}

export function MultiSelectCombobox({
  items,
  placeholder,
  searchPlaceholder,
  emptyMessage,
  selectedValues,
  onValueChange,
  className,
  disabled = false,
}: MultiSelectComboboxProps) {
  const [open, setOpen] = React.useState(false);

  const selectedItems = items.filter((item) =>
    selectedValues.includes(item.value)
  );

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          role="combobox"
          aria-expanded={open}
          className={cn("w-full justify-between", className)}
          disabled={disabled}
        >
          <div className="flex gap-1 items-center">
            {selectedItems.length > 0 ? (
              <div className="flex flex-nowrap gap-1 max-w-[90%]">
                {selectedItems.map((item) => (
                  <Badge
                    key={item.value}
                    className={cn(item.color)}
                    onClick={(e) => {
                      e.stopPropagation();
                      onValueChange(
                        selectedValues.filter((value) => value !== item.value)
                      );
                    }}
                  >
                    {item.label}
                    <X className="ml-1 h-3 w-3" />
                  </Badge>
                ))}
              </div>
            ) : (
              <span className="text-muted-foreground">{placeholder}</span>
            )}
          </div>
          <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
        </Button>
      </PopoverTrigger>
      <PopoverContent className="p-0 w-[var(--radix-popover-trigger-width)] min-w-0">
        <Command>
          <CommandInput placeholder={searchPlaceholder} className="h-9" />
          <CommandList>
            <CommandEmpty>{emptyMessage}</CommandEmpty>
            <CommandGroup>
              {items.map((item) => (
                <CommandItem
                  key={item.value}
                  value={item.label}
                  onSelect={() => {
                    const newSelectedValues = selectedValues.includes(
                      item.value
                    )
                      ? selectedValues.filter((value) => value !== item.value)
                      : [...selectedValues, item.value];
                    onValueChange(newSelectedValues);
                  }}
                >
                  <div className="flex items-center">
                    <Check
                      className={cn(
                        "mr-2 h-4 w-4",
                        selectedValues.includes(item.value)
                          ? "opacity-100"
                          : "opacity-0"
                      )}
                    />
                    {item.label}
                  </div>
                </CommandItem>
              ))}
            </CommandGroup>
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  );
}
