interface CategoryLayoutProps {
  title?: string;
  subtitle?: string;
  children?: React.ReactNode;
}

const CategoryLayout = ({ title, subtitle, children }: CategoryLayoutProps) => {
  return (
    <div className="flex flex-col gap-4 py-3">
      <div className="text-center mb-6">
        <h2 className="text-2xl font-semibold mb-2">{title}</h2>
        <p className="text-muted-foreground">{subtitle}</p>
      </div>
      {children}
    </div>
  );
};

export default CategoryLayout;
