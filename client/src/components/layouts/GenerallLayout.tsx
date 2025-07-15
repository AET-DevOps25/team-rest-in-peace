interface GenerallLayoutProps {
  children: React.ReactNode;
}

const GenerallLayout = ({ children }: GenerallLayoutProps) => {
  return (
    <div className="flex flex-col min-w-0 max-w-[100rem] w-full h-full mx-auto p-4 gap-4">
      {children}
    </div>
  );
};

export default GenerallLayout;
